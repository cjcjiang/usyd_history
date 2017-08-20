from Crypto.Hash import SHA256
from Crypto.Random import random

from lib.helpers import read_hex

# Project TODO: Is this the best choice of prime? Why? Why not? Feel free to replace!

# 1536 bit safe prime for Diffie-Hellman key exchange
# obtained from RFC 3526
raw_prime = """FFFFFFFF FFFFFFFF C90FDAA2 2168C234 C4C6628B 80DC1CD1
29024E08 8A67CC74 020BBEA6 3B139B22 514A0879 8E3404DD
EF9519B3 CD3A431B 302B0A6D F25F1437 4FE1356D 6D51C245
E485B576 625E7EC6 F44C42E9 A637ED6B 0BFF5CB6 F406B7ED
EE386BFB 5A899FA5 AE9F2411 7C4B1FE6 49286651 ECE45B3D
C2007CB8 A163BF05 98DA4836 1C55D39A 69163FA8 FD24CF5F
83655D23 DCA3AD96 1C62F356 208552BB 9ED52907 7096966D
670C354E 4ABC9804 F1746C08 CA237327 FFFFFFFF FFFFFFFF"""
# Convert from the value supplied in the RFC to an integer
prime = read_hex(raw_prime)
#prime = 13
# Project TODO: write the appropriate code to perform DH key exchange

def calculate_g_a(g,a):
    g_a = pow(g,a,prime)
    return (g_a)

def create_dh_key():
    # Creates a Diffie-Hellman key
    # Returns (public, private)
	#print("prime:",prime)
	g = random.randint(1,prime)
	#print("g:",g)
	a = random.randint(1,prime-1)
	#print("a:",a)
	# g_a is the g to the power of a mod p
	g_a = pow(g,a,prime)
	#print("g-a:",g_a)
	return (g_a, a, g)

def calculate_dh_secret(their_public, my_private):
    # Calculate the shared secret
	shared_secret = pow(their_public,my_private,prime)
	#print("shared-secret:",shared_secret)
    # Hash the value so that:
    # (a) There's no bias in the bits of the output
    #     (there may be bias if the shared secret is used raw)
    # (b) We can convert to raw bytes easily
    # (c) We could add additional information if we wanted
    # Feel free to change SHA256 to a different value if more appropriate
	#test = bytes(str(shared_secret), "ascii")
	#print(test)
	shared_hash = SHA256.new(bytes(str(shared_secret), "ascii")).hexdigest()
	#print("shared-hash:",shared_hash)
	return shared_hash
