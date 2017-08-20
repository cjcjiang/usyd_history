import struct

from Crypto.Cipher import XOR

#write by jiang
from Crypto.Cipher import AES
from Crypto import Random

from dh import create_dh_key, calculate_dh_secret, calculate_g_a

class StealthConn(object):
    def __init__(self, conn, client=False, server=False, verbose=False):
        self.conn = conn
        self.cipher = None
        self.client = client
        self.server = server
        self.verbose = verbose
		
		#give an empty iv, changed by jiang
        self.inital_vector = Random.new().read(AES.block_size)
		#give an empty aes key
        self.aes_key = 0
		
        self.initiate_session()

    def initiate_session(self):
        # Perform the initial connection handshake for agreeing on a shared secret

        ### TODO: Your code here!
        # This can be broken into code run just on the server or just on the client
        if self.server or self.client:
            #add g,jiang
            my_public_key, my_private_key, g = create_dh_key()
            # Send them our public key
            self.send(bytes(str(my_public_key), "ascii"))
            # Receive their public key
            their_public_key = int(self.recv())
			
            #send client's g,jiang
            if self.client:
                self.send(bytes(str(g), "ascii"))
                their_public_key = int(self.recv())
			
			#in the server,re calculate g_a,jiang
            if self.server:
                g = int(self.recv())
                print("g:",g)
                my_public_key = calculate_g_a(g,my_private_key)
                self.send(bytes(str(my_public_key), "ascii"))
			
            # Obtain our shared secret
            shared_hash = calculate_dh_secret(their_public_key, my_private_key)
            print("Shared hash: {}".format(shared_hash))

        # Default XOR algorithm can only take a key of length 32
        #self.cipher = XOR.new(shared_hash[:4])
		#the original file use one time padding
		#jiang change it to aes128, key size 16bytes, mode cfb
        self.aes_key = shared_hash[:16]
        self.cipher =  AES.new(self.aes_key, AES.MODE_CFB, self.inital_vector)

    def send(self, data):
        if self.cipher:
            #encrypted_data = self.cipher.encrypt(data)
			
			#calculate the encrypted data, jiang change it to aes128, iv is included 
			#re generate iv, and re generate the cipher
            self.inital_vector = Random.new().read(AES.block_size)
            self.cipher =  AES.new(self.aes_key, AES.MODE_CFB, self.inital_vector)
            print("self.inital_vector",self.inital_vector)
            encrypted_data = self.inital_vector + self.cipher.encrypt(data)
            print("encrypted_data:",encrypted_data)
			
            if self.verbose:
                print("Original data: {}".format(data))
                print("Encrypted data: {}".format(repr(encrypted_data)))
                print("Sending packet of length {}".format(len(encrypted_data)))
        else:
            encrypted_data = data

        # Encode the data's length into an unsigned two byte int ('H')
        pkt_len = struct.pack('H', len(encrypted_data))
        self.conn.sendall(pkt_len)
        self.conn.sendall(encrypted_data)

    def recv(self):
        # Decode the data's length from an unsigned two byte int ('H')
        pkt_len_packed = self.conn.recv(struct.calcsize('H'))
        unpacked_contents = struct.unpack('H', pkt_len_packed)
        pkt_len = unpacked_contents[0]

        encrypted_data = self.conn.recv(pkt_len)
        if self.cipher:
            #data = self.cipher.decrypt(encrypted_data)
			
			#jiang change it to aes decrypt
            self.inital_vector = encrypted_data[:16]
            encrypted_message = encrypted_data[16:]
            self.cipher =  AES.new(self.aes_key, AES.MODE_CFB, self.inital_vector)
            data = self.cipher.decrypt(encrypted_message)
			
            if self.verbose:
                print("Receiving packet of length {}".format(pkt_len))
                print("Encrypted data: {}".format(repr(encrypted_data)))
                print("Original data: {}".format(data))
        else:
            data = encrypted_data

        return data

    def close(self):
        self.conn.close()
