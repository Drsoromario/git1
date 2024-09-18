import random
import string

def generate_password(length, use_uppercase, use_lowercase, use_digits, use_symbols):
    if length < 11 or length > 30:
        raise ValueError("パスワードの長さは11文字から30文字の間で指定してください。")

    character_pool = ""
    if use_uppercase:
        character_pool += string.ascii_uppercase
    if use_lowercase:
        character_pool += string.ascii_lowercase
    if use_digits:
        character_pool += string.digits
    if use_symbols:
        character_pool += string.punctuation

    if not character_pool:
        raise ValueError("少なくとも1つの文字タイプを選択してください。")

    password = ''.join(random.choice(character_pool) for _ in range(length))
    return password

# 使用例
length = 16
use_uppercase = True
use_lowercase = True
use_digits = True
use_symbols = True

password = generate_password(length, use_uppercase, use_lowercase, use_digits, use_symbols)
print("生成されたパスワード:", password)
