import base64
import jwt  # PyJWT
import datetime
import time
import argparse
import os

default_payload = {
    'sub': 'testuser',
    'name': 'test user',
    'roles': ['default'],
    "iat": int(time.time()),
    "exp": int(time.time()) + 300
}

def generate_token(private_key_filename, payload) -> str:
    assert os.path.exists(private_key_filename), f"file {private_key_filename} NO found"
    with open(private_key_filename, 'r') as fd:
        token = jwt.encode(payload, fd.read(), algorithm="RS256")
        return token

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='generate json web token with private keys (RSA)')
    parser.add_argument('-k', '--key', help='private key file', default='private.pem')
    parser.add_argument('-s', '--subject', help='set custom subject claim', default='testuser')
    parser.add_argument('-r', '--roles', help='comma separated list of roles', default='default')
    parser.add_argument('--validity', help='token validity in seconds', default=300, type=int)
    args = parser.parse_args()

    default_payload['sub'] = args.subject
    default_payload['exp'] = int(time.time()) + args.validity
    default_payload['roles'] = args.roles.split(',')

    print(generate_token(args.key, default_payload))

