from Crypto.Util import strxor

m1 = "Someone's sniffing around. Destroy the fi".encode("ascii")
c1 = [57, 221, 18, 189, 243, 62, 252, 122, 94, 81, 20, 90, 181, 205, 104, 226, 41, 14, 79, 124, 149, 97, 170, 246, 157, 88, 97, 151, 199, 56, 249, 191, 252, 182, 203, 31, 147, 108, 125, 166, 72, 145, 49, 60]
c2 = [62, 218, 26, 248, 204, 34, 240, 48, 72, 81, 10, 93, 178, 194, 125, 255, 34, 27, 72, 110, 199, 125, 175, 253, 156, 21, 41, 243, 203, 56, 173, 172, 231, 239, 184, 34, 175, 41, 108, 241, 21]
c3 = []

#print(m1)

for i in range(len(c2)):
    #print(c1[i])
    #print(i)
    c3.append(c1[i]^c2[i])

print(c3)
c3 = bytes(c3)
print(c3)

m2 = strxor.strxor(c3,m1)

print(m2)
m2 = bytes(m2).decode("ascii")
print(m2)
