from Crypto.Signature import PKCS1_v1_5
from Crypto.Hash import SHA
from Crypto.PublicKey import RSA

message = 'i am jiang'
mes_encoded = message.encode("ascii")
key = RSA.importKey(open('mykeyprivate.pem').read())
h = SHA.new(mes_encoded)
signer = PKCS1_v1_5.new(key)
signature = signer.sign(h)

# signature is bytes
print(signature)

key_verifier = RSA.importKey(open('mykeypublic.pem').read())
h_verifier = SHA.new(mes_encoded)
verifier = PKCS1_v1_5.new(key_verifier)
if verifier.verify(h_verifier, signature):
    print("The signature is authentic.")
else:
    print("The signature is not authentic.")

mes_wrong = "i am yu"
mes_wrong_encoded = mes_wrong.encode("ascii")
key_verifier_wrong = RSA.importKey(open('mykeypublic.pem').read())
h_verifier_wrong = SHA.new(mes_wrong_encoded)
verifier_wrong = PKCS1_v1_5.new(key_verifier_wrong)
if verifier_wrong.verify(h_verifier_wrong, signature):
    print("The signature is authentic.")
else:
    print("The signature is not authentic.")
