
1. Generate private key
> openssl genrsa -out private.pem 2048

2. generate public key
> openssl rsa -in private.pem -pubout -out public.pem


3. Run **generate.py** to generate tokens