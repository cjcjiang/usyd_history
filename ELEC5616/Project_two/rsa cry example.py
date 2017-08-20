from Crypto.Cipher import PKCS1_v1_5
from Crypto.PublicKey import RSA
from Crypto.Hash import SHA

from Crypto import Random

message = 'To be encrypted'
mes_en = message.encode('ascii')
#h = SHA.new(mes_en)

key = RSA.importKey(open('pub.pem').read())
cipher = PKCS1_v1_5.new(key)
#ciphertext = cipher.encrypt(mes_en+h.digest())
ciphertext = cipher.encrypt(mes_en)

key_de = RSA.importKey(open('keytest').read())

dsize = SHA.digest_size
sentinel = Random.new().read(15+dsize)      # Let's assume that average data length is 15

cipher_de = PKCS1_v1_5.new(key_de)
message_de = cipher_de.decrypt(ciphertext, sentinel)


#digest = SHA.new(message_de[:-dsize]).digest()
#if digest==message_de[-dsize:]:                # Note how we DO NOT look for the sentinel
#    print("Encryption was correct.")
#else:
#   print("Encryption was not correct.")

print(message_de)
