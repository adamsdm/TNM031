
from random import choice
from string import ascii_letters
from crypto import encrypt_file, decrypt_file
import os, sys

def main():
    decrypt_mode = False
    if(len(sys.argv) > 1):
        print("Decrypting files")
        decrypt_mode = True
        key = sys.argv[1]
    else:
        key = ''.join(choice(ascii_letters) for i in range(32))
        f = open('key.txt', 'w')
        f.write(key)
        print("Encrypting files")

    root_path = "./encryptMe"
    
    
    for path, dirs, files in os.walk(root_path):
        for f in files:
            file_path = path + '/' + f
            if decrypt_mode:
                decrypt_file(key, file_path)
                os.remove(file_path)
            else:
                encrypt_file(key, file_path)
                os.remove(file_path)    

main()