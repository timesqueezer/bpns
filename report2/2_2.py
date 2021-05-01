from hashlib import md5
from itertools import product

target_hash = '2b2935865b8a6749b0fd31697b467bd7'
salt = '8kofferradio'.encode('utf-8')
alphabet_lowercase = 'abcdefghijklmopqrstuvwxyz'
alphabet = (
    [c for c in alphabet_lowercase] +
    [str(n) for n in range(10)]
)
passwort_max_length = 6

if __name__ == '__main__':
    for l in range(1, passwort_max_length):
        for pw_char_list in product(alphabet, repeat=l):
            pw = ''.join(pw_char_list).encode('utf-8')
            h = md5(salt + pw)
            if h.hexdigest() == target_hash:
                print('Found passwort:', pw)
                quit()
