import struct
from Crypto.Cipher import XOR

# Import to do key exchange, confidentiality, 
# Integrity and preventing relay attach
from Crypto.Cipher import AES
from Crypto import Random
from Crypto.Hash import HMAC
from Crypto.Hash import SHA256
from dh import create_dh_key, calculate_dh_secret, calculate_g_a


class StealthConn(object):
    # Initialization. These variable "client", "server" and so on 
    # will be changed according to the nature of the socket
    def __init__(self, conn, client=False, server=False, verbose=False):
        self.conn = conn
        self.cipher = None
        self.client = client
        self.server = server
        self.verbose = verbose
		
		# Use a random number as the initial vector of AES
        self.inital_vector = Random.new().read(AES.block_size)
        
		# Give a key, which will be asigned value later
        self.aes_key = 0
		
        # To prevent replay attack, we attach a random number to the 
        # message, thus the uniqueness of each message could be 
        # ensured
        self.replay_attack_random = Random.get_random_bytes(14)
				
		# prepare a list to store the random
        self.random_list = []		
		
        # initiate_session begins
        self.initiate_session()

        
        
    # The initiate session function
    def initiate_session(self):
        # Perform the initial connection handshake for agreeing on a shared secret
        ### TODO: Your code here!
        # This can be broken into code run just on the server or just on the client
        if self.server or self.client:
        
            # Create the DH key and store into these two variables
            # "my_private_key" is a
            # "my_public_key" is the g_a, 
            # which means g to the power of a mod p
            my_public_key, my_private_key = create_dh_key()
            
            # Send out public key
            # For client, it sends g_a as "my_public_key"
            self.send(bytes(str(my_public_key), "ascii"))
            
            # For server, receive public key g_a as "their_public_key"
            their_public_key = int(self.recv())
			
            # The following codes was designed to send g. However, according to 
            # rfc3521, the g should be 2, instead of a random number
            # Thus the codes has been abandoned
            '''
            # send client's g
            if self.client:
                self.send(bytes(str(my_public_key), "ascii"))
                self.send(bytes(str(g), "ascii"))
                their_public_key = int(self.recv())
			
			in the server,re calculate g_a
            if self.server:
                their_public_key = int(self.recv())
                g = int(self.recv())
                #print("g:",g)
                my_public_key = calculate_g_a(g,my_private_key)
                self.send(bytes(str(my_public_key), "ascii"))
			'''
            
            # Obtain hased shared secret
            shared_hash = calculate_dh_secret(their_public_key, my_private_key)
            print("Shared hash: {}".format(shared_hash))        

		# The original file use XOR one time padding 
		# and we changed it to aes128, key size 16bytes, mode cfb       
        # Becuase the key size is 16 bytes, we take 16 bytes of 
        # the hashed shared key
        self.aes_key = shared_hash[:16]
        
        # set self.cipher object so that it will enter the "if" in "send" function
        self.cipher = AES.new(self.aes_key, AES.MODE_CFB, self.inital_vector)
    
    # The send function just in client
    def send(self, data):
        # Only after the encryption is done, the client will send encrypted msg
        # Or, it will just send the original, as used to send g_a
        if self.cipher:
			# Calculate the encrypted data
			# Generate initial vector, and the cipher objest
            self.inital_vector = Random.new().read(AES.block_size)
            self.cipher = AES.new(self.aes_key, AES.MODE_CFB, self.inital_vector)
			
			# To prevent replay attack, we add 14 random bytes 
            # to the start of the message to ensure the uniqueness
            self.replay_attack_random = Random.get_random_bytes(14)
            data = self.replay_attack_random + data
			
            # Attach the initial vector to the encreypted msg
            encrypted_msg = self.inital_vector + self.cipher.encrypt(data)
            
            # Do HMAC to the msg to be sent
            encrypted_msg_hmac = HMAC.new(bytes(self.aes_key,'ascii'), encrypted_msg, SHA256)
            encrypted_msg_hash = encrypted_msg_hmac.hexdigest()
            
            #print("encrypted_msg_hash:",encrypted_msg_hash)
            
            # Attach the hash to the start of encrypted msg to bytes
            encrypted_data = bytes(encrypted_msg_hash,'ascii') + encrypted_msg
            
            # Print out to debug
            if self.verbose:
                print("Original data: {}".format(data))
                print("Encrypted data: {}".format(repr(encrypted_data)))
                print("Sending packet of length {}".format(len(encrypted_data)))
                
        # If self.cipher is not set, then it means the "send" is just to send g_a
        else:
            encrypted_data = data

        # Encode the data's length into an unsigned two byte int ('H')
        pkt_len = struct.pack('H', len(encrypted_data))
        self.conn.sendall(pkt_len)
        self.conn.sendall(encrypted_data)
    
    
    
    # The recv function just in server
    def recv(self):
        # Decode the data's length from an unsigned two byte int ('H')
        pkt_len_packed = self.conn.recv(struct.calcsize('H'))
        unpacked_contents = struct.unpack('H', pkt_len_packed)
        pkt_len = unpacked_contents[0]
        
        # The received data stored in "encrypted_data"
        encrypted_data = self.conn.recv(pkt_len)
        
        # Again, only after the encryption is done, the server will do decryption
        # Otherwise, it will just receive the original, as used to receive g_a
        if self.cipher:
        
			# Firstly, take out the hash to check integrity
            hash = encrypted_data[:64]
            #print("hash:",hash)           
            encrypted_msg_hmac = HMAC.new(bytes(self.aes_key,'ascii'), encrypted_data[64:], SHA256)
            encrypted_msg_hash = encrypted_msg_hmac.hexdigest()
            # encrypted_msg_hash = "abc"
            
            # If the hash matches, then do the decryption
            # Otherwise shut down the connection
            if bytes(encrypted_msg_hash,'ascii') == hash:
                print("Hash is right, message is authentic")
                
                # Take out the seperate the initial vector and encrypted msg
                self.inital_vector = encrypted_data[64:80]
                encrypted_message = encrypted_data[80:]
                
                # Set the self.cipher object
                self.cipher =  AES.new(self.aes_key, AES.MODE_CFB, self.inital_vector)
                
                # At this point, the msg consists of the data to send, 
                # and the random number to prevent replay attack
                # We need to seperate them
                random_and_data = self.cipher.decrypt(encrypted_message)
                send_random_str = random_and_data[:14]
                data = random_and_data[14:]
                
                # Check if it is replayed. If it is, 
                # then shut down the connection
                if send_random_str in self.random_list:
                    data = bytes("replay attack","ascii")
                    print("this is replay attack")
                    
                # If is not replayed, then append it to the list
                else:
                    self.random_list.append(send_random_str)
				
                
            # If hash dose not matches, then shut down the connection
            else:
                print("Hash is wrong, message has been tampered")
                print("Connection shut down")
                data = "Hash is wrong, message has been tampered"
                self.conn.close()
			
            if self.verbose:
                print("Receiving packet of length {}".format(pkt_len))
                print("Encrypted data: {}".format(repr(encrypted_data)))
                print("Original data: {}".format(data))
        else:
            data = encrypted_data

        return data

    def close(self):
        self.conn.close()
