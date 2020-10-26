### Blockchain
This is an example blockchain project written in Java.

Blocks are created and given a hash identifier, using a randomly generated magic number and hashing algorithm.
A block also contains the hash of the previous block, so altering the blockchain becomes harder as anyone attempting to
do so needs to generate a new magic number and hash but not only for the current block but all previous blocks, 
otherwise the blockchain becomes invalid. 

To create a proof of work concept, a magic number is generated before the Block object is hashed, and a
condition of *n* zeroes prefixed to the hash must be met before the hash is accepted. This in effect means it is 
impossible to alter a block in the Blockchain as new blocks are added faster than it would be possible to alter the 
entire blockchain if tampering with a individual block.

### Blockchain Messenger
A Java Swing GUI client that sends transactions to 
a server, using a Socket endpoint, which is generating 
blocks for the blockchain. This messenger uses digital 
signatures to authenticate communication. Public and private
keys are generated using RSA cryptosystem. The public key is
sent to the server, which it uses to decrypt the private key 
encrypted transaction before adding the transaction to the block being
generated.

![Coin Messenger](https://github.com/SiAust/Blockchain/blob/master/messenger-gui.jpg)