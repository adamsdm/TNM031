import glob
import os, random, struct
from Crypto.Cipher import AES
from string import ascii_letters

def encrypt_file(key, infilepath, outfilepath=None):
    '''
    Uses AES cryptation to encrypt a file
    '''

    if not outfilepath:
        outfilepath = infilepath + '.enc'

    chunksize=64*1024

    iv = os.urandom(16) 
    encryptor = AES.new(key ,AES.MODE_CBC, iv)
    fileSize = os.path.getsize(infilepath)

    with open(infilepath, 'rb') as infile:
        with open(outfilepath, 'wb') as outfile:
            outfile.write(struct.pack('<Q', fileSize))
            outfile.write(iv)

            while True:
                chunk = infile.read(chunksize)
                if len(chunk) == 0:
                    break
                elif len(chunk) % 16 != 0:
                    chunk += b' ' * (16 - len(chunk) % 16)

                outfile.write(encryptor.encrypt(chunk))

def decrypt_file(key, infilepath, outfilepath=None):
    """ 
    Decrypts a file with the given key
    """
    if not outfilepath:
        outfilepath = os.path.splitext(infilepath)[0]

    chunksize=24*1024

    with open(infilepath, 'rb') as infile:
        originalSize = struct.unpack('<Q', infile.read(struct.calcsize('Q')))[0]
        iv = infile.read(16)
        decryptor = AES.new(key, AES.MODE_CBC, iv)

        with open(outfilepath, 'wb') as outfile:
            while True:
                chunk = infile.read(chunksize)
                if len(chunk) == 0:
                    break
                outfile.write(decryptor.decrypt(chunk))

            outfile.truncate(originalSize)
