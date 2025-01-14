import tkinter as tk
from tkinter import messagebox
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad, unpad
import base64

# AES256 の暗号化クラス
class AES256Cipher:
    def __init__(self, key):
        self.key = key

    def encrypt(self, plaintext):
        cipher = AES.new(self.key, AES.MODE_CBC)
        ct_bytes = cipher.encrypt(pad(plaintext.encode('utf-8'), AES.block_size))
        iv = base64.b64encode(cipher.iv).decode('utf-8')
        ct = base64.b64encode(ct_bytes).decode('utf-8')
        return f"{iv}:{ct}"

    def decrypt(self, ciphertext):
        try:
            iv, ct = ciphertext.split(":")
            iv = base64.b64decode(iv)
            ct = base64.b64decode(ct)
            cipher = AES.new(self.key, AES.MODE_CBC, iv)
            pt = unpad(cipher.decrypt(ct), AES.block_size)
            return pt.decode('utf-8')
        except Exception as e:
            raise ValueError("復号に失敗しました。入力が正しいか確認してください。")

# GUI アプリケーションの構築
def run_gui():
    def process_action():
        action = action_var.get()
        key = key_entry.get()
        data = data_entry.get("1.0", tk.END).strip()

        if not key or len(key) != 32:
            messagebox.showerror("エラー", "共通鍵は32文字で入力してください。")
            return

        if not data:
            messagebox.showerror("エラー", "データを入力してください。")
            return

        cipher = AES256Cipher(key.encode('utf-8'))

        try:
            if action == "encrypt":
                result = cipher.encrypt(data)
                result_label.config(text="暗号化結果:")
            elif action == "decrypt":
                result = cipher.decrypt(data)
                result_label.config(text="復号化結果:")
            result_text.delete("1.0", tk.END)
            result_text.insert(tk.END, result)
        except Exception as e:
            messagebox.showerror("エラー", str(e))

    root = tk.Tk()
    root.title("AES256 暗号化/復号化ツール")

    tk.Label(root, text="操作選択:").pack(anchor="w")
    action_var = tk.StringVar(value="encrypt")
    tk.Radiobutton(root, text="暗号化", variable=action_var, value="encrypt").pack(anchor="w")
    tk.Radiobutton(root, text="復号化", variable=action_var, value="decrypt").pack(anchor="w")

    tk.Label(root, text="共通鍵 (32文字):").pack(anchor="w")
    key_entry = tk.Entry(root, show="*", width=40)
    key_entry.pack(fill="x", padx=5, pady=5)

    tk.Label(root, text="データ入力:").pack(anchor="w")
    data_entry = tk.Text(root, height=10, width=50)
    data_entry.pack(fill="x", padx=5, pady=5)

    process_button = tk.Button(root, text="実行", command=process_action)
    process_button.pack(pady=10)

    result_label = tk.Label(root, text="結果:")
    result_label.pack(anchor="w")

    result_text = tk.Text(root, height=10, width=50, state="normal")
    result_text.pack(fill="x", padx=5, pady=5)

    root.mainloop()

if __name__ == "__main__":
    run_gui()
