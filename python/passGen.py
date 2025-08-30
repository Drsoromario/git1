#!/usr/bin/env python3
# password_generator_gui.py
# Requires Python 3.x (Tkinter included in standard distribution)

import secrets
import string
import tkinter as tk
from tkinter import ttk
from tkinter import messagebox

# ---------- Configuration ----------
MIN_LENGTH = 4
MAX_LENGTH = 512
DEFAULT_LENGTH = 16
DEFAULT_COUNT = 5
SYMBOLS = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~"  # カスタマイズ可
# -----------------------------------

class PasswordGeneratorApp(tk.Tk):
    def __init__(self):
        super().__init__()
        self.title("Password Generator")
        self.resizable(False, False)
        self.create_widgets()
        self.password_vars = []  # 生成されたパスワードを保存する StringVar のリスト
        self.masked = True

    def create_widgets(self):
        frm = ttk.Frame(self, padding=12)
        frm.grid(row=0, column=0, sticky="nsew")

        # --- Options frame ---
        opt_frame = ttk.LabelFrame(frm, text="Options", padding=8)
        opt_frame.grid(row=0, column=0, sticky="ew")

        ttk.Label(opt_frame, text="Length:").grid(row=0, column=0, sticky="w")
        self.len_var = tk.IntVar(value=DEFAULT_LENGTH)
        self.len_spin = ttk.Spinbox(opt_frame, from_=MIN_LENGTH, to=MAX_LENGTH, textvariable=self.len_var, width=6)
        self.len_spin.grid(row=0, column=1, sticky="w", padx=(4, 12))

        ttk.Label(opt_frame, text="Count:").grid(row=0, column=2, sticky="w")
        self.count_var = tk.IntVar(value=DEFAULT_COUNT)
        self.count_spin = ttk.Spinbox(opt_frame, from_=1, to=200, textvariable=self.count_var, width=6)
        self.count_spin.grid(row=0, column=3, sticky="w", padx=(4, 12))

        # Character type checkboxes
        self.use_digits = tk.BooleanVar(value=True)
        self.use_lower = tk.BooleanVar(value=True)
        self.use_upper = tk.BooleanVar(value=True)
        self.use_symbols = tk.BooleanVar(value=False)

        ttk.Checkbutton(opt_frame, text="Digits (0-9)", variable=self.use_digits).grid(row=1, column=0, sticky="w", pady=(6,0))
        ttk.Checkbutton(opt_frame, text="Lowercase (a-z)", variable=self.use_lower).grid(row=1, column=1, sticky="w", pady=(6,0))
        ttk.Checkbutton(opt_frame, text="Uppercase (A-Z)", variable=self.use_upper).grid(row=1, column=2, sticky="w", pady=(6,0))
        ttk.Checkbutton(opt_frame, text="Symbols", variable=self.use_symbols).grid(row=1, column=3, sticky="w", pady=(6,0))

        # Generate button and show/hide toggle
        btn_frame = ttk.Frame(frm)
        btn_frame.grid(row=1, column=0, sticky="ew", pady=(8, 0))

        self.gen_button = ttk.Button(btn_frame, text="Generate", command=self.generate_passwords)
        self.gen_button.grid(row=0, column=0, sticky="w")

        self.toggle_button = ttk.Button(btn_frame, text="Show", command=self.toggle_visibility)
        self.toggle_button.grid(row=0, column=1, padx=(8,0), sticky="w")

        # Results frame
        self.results_frame = ttk.LabelFrame(frm, text="Passwords", padding=8)
        self.results_frame.grid(row=2, column=0, sticky="nsew", pady=(8,0))

        # Initially empty; entries added after generation

    def build_charset(self):
        parts = []
        if self.use_digits.get():
            parts.append(string.digits)
        if self.use_lower.get():
            parts.append(string.ascii_lowercase)
        if self.use_upper.get():
            parts.append(string.ascii_uppercase)
        if self.use_symbols.get():
            parts.append(SYMBOLS)
        return "".join(parts)

    def validate_options(self, length, count, charset):
        if length < MIN_LENGTH or length > MAX_LENGTH:
            messagebox.showerror("Invalid length", f"Length must be between {MIN_LENGTH} and {MAX_LENGTH}.")
            return False
        if count < 1 or count > 200:
            messagebox.showerror("Invalid count", "Count must be between 1 and 200.")
            return False
        if not charset:
            messagebox.showerror("No character sets", "Please select at least one character type.")
            return False
        return True

    def generate_single(self, charset, length):
        # Use secrets.choice for cryptographic randomness
        return "".join(secrets.choice(charset) for _ in range(length))

    def clear_old_results(self):
        for child in self.results_frame.winfo_children():
            child.destroy()
        self.password_vars.clear()

    def generate_passwords(self):
        try:
            length = int(self.len_var.get())
            count = int(self.count_var.get())
        except Exception:
            messagebox.showerror("Invalid input", "Length and Count must be integers.")
            return

        charset = self.build_charset()
        if not self.validate_options(length, count, charset):
            return

        self.clear_old_results()

        # Create results rows
        for i in range(count):
            var = tk.StringVar()
            # generate immediately
            var.set(self.generate_single(charset, length))
            self.password_vars.append(var)

            row_frame = ttk.Frame(self.results_frame)
            row_frame.grid(row=i, column=0, sticky="ew", pady=2)

            show_text = self.masked_text(var.get()) if self.masked else var.get()
            ent = ttk.Entry(row_frame, textvariable=var, width=60)
            ent.grid(row=0, column=0, sticky="w")
            # Prevent direct editing? Keep editable to allow user changes; optional:
            # ent.config(state="readonly")

            copy_btn = ttk.Button(row_frame, text="Copy", command=lambda v=var: self.copy_to_clipboard(v.get()))
            copy_btn.grid(row=0, column=1, padx=(6,0))
        # Update toggle button text based on mask state
        self.update_entry_display()

    def masked_text(self, text):
        return "•" * len(text)

    def update_entry_display(self):
        # Iterate through entries and mask/unmask based on self.masked
        for child in self.results_frame.winfo_children():
            for w in child.winfo_children():
                if isinstance(w, ttk.Entry) or isinstance(w, tk.Entry):
                    # get associated StringVar via widget cget / variable retrieval
                    var_name = w.cget("textvariable")
                    if var_name:
                        var = self.nametowidget(var_name) if var_name.startswith("!") else None
                    # Simpler: read the current value and replace display via entry.delete/insert
                    current = w.get()
                    # Find original value in password_vars matching length; safer to just set show char
                    if self.masked:
                        # use show option on Entry widget
                        try:
                            w.configure(show="•")
                        except tk.TclError:
                            pass
                    else:
                        try:
                            w.configure(show="")
                        except tk.TclError:
                            pass
        self.toggle_button.config(text="Show" if self.masked else "Hide")

    def toggle_visibility(self):
        # Toggle masked state and update entry displays
        self.masked = not self.masked
        self.update_entry_display()

    def copy_to_clipboard(self, text):
        if not text:
            return
        self.clipboard_clear()
        self.clipboard_append(text)
        # Small visual feedback
        messagebox.showinfo("Copied", "Password copied to clipboard.")

if __name__ == "__main__":
    app = PasswordGeneratorApp()
    app.mainloop()

