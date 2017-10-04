from crypto import encrypt_file, decrypt_file
from tkinter import *
from os import walk, remove

root = Tk()
root.title("Decryptor")

def decrypt(key):
    if(len(key)==32): # Only allow keys with 32 bit length (set in encrypter)
        root_path = "./encryptMe"

        try:
            for path, dirs, files in walk(root_path):
                for f in files:
                    file_path = path + '/' + f
                    decrypt_file(key, file_path)
                    remove(file_path)
            statusLabel = Label(root, text="Files decrypted", bg="green")
            statusLabel.config(font=("Courier", 22))
            statusLabel.pack()
        except:
            statusLabel = Label(root, text="Something went wrong", bg="red")
            statusLabel.config(font=("Courier", 22))
            statusLabel.pack()
            

        


photo = PhotoImage(file="banner.gif")
label = Label(root, image=photo)
label.pack()

keyLabel = Label(root, text="Key:")
keyLabel.config(font=("Courier", 22))
keyLabel.pack()

keyEntry = Entry(root, font=("Courier", 22), width=32)
keyEntry.pack()

decryptButton = Button(root,padx=30, text="Decrypt",bg="green" ,  font=("Courier", 22), command=lambda: decrypt(keyEntry.get()))
decryptButton.pack()


root.mainloop()