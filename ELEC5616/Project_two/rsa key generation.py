from Crypto.PublicKey import RSA

key = RSA.generate(2048)
f = open('mykey.pem','wb')
f.write(key.exportKey('PEM'))
f.close()

pubkey = key.publickey()
fv = open('mykeypublic.pem','wb')
fv.write(pubkey.exportKey('PEM'))
fv.close()
