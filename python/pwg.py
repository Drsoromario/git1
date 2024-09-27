import random
import string

def generate_password(length, use_digits, use_uppercase, use_lowercase, use_symbols):
    symbols = "~!@#%^&*()+{}[]?=-"
    characters = ""
    if use_digits:
        characters += string.digits
    if use_uppercase:
        characters += string.ascii_uppercase
    if use_lowercase:
        characters += string.ascii_lowercase
    if use_symbols:
        characters += symbols

    if not characters:
        raise ValueError("At least one character set must be selected")

    password = ''.join(random.choice(characters) for _ in range(length))
    return password

def main():
    try:
        length = int(input("Enter the length of the password (16-200): "))
        if length < 16 or length > 200:
            raise ValueError("Password length must be between 16 and 200")

        count = int(input("Enter the number of passwords to generate: "))
        if count <= 0:
            raise ValueError("The number of passwords must be greater than 0")

        use_digits = input("Include digits? (y/n): ").lower() == 'y'
        use_uppercase = input("Include uppercase letters? (y/n): ").lower() == 'y'
        use_lowercase = input("Include lowercase letters? (y/n): ").lower() == 'y'
        use_symbols = input("Include symbols? (y/n): ").lower() == 'y'

        for i in range(1, count + 1):
            print(f"{i}: {generate_password(length, use_digits, use_uppercase, use_lowercase, use_symbols)}")

    except ValueError as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    main()
