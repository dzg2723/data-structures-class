# Darren Good
# Advanced Data Structures
# 02/19/19 - 02/22/19

#https://www.thepythoncorner.com/2016/12/object-serialization-in-python/?doing_wp_cron=1550799828.2877969741821289062500
import pickle   #Serialization module
import sys, queue

"""
Nodes to be used within the HuffTree class.
The char parameter is used to store a single character
but will be None if the node is not a leaf.
"""
class HuffNode():

    def __init__(self, char, leftChild=None, rightChild=None):
        self.char = char
        self.leftChild = leftChild
        self.rightChild = rightChild

"""
A tree class to be used for Huffman Encoding.
"""
class HuffTree():

    eof = "256" #End of File character to know when compressed data ends

    def __init__(self, weight, root):
        self.weight = weight
        self.root = root
        self.storedData = None #Store compressed data in tree for serialization

    def addHuffTree(self, huffTree):
        """
        Creates a "new" HuffTree by combining self and a given
        huffTree. Makes a new HuffNode as root with its children
        being the nodes that were roots for self and the given tree.
        """
        self.root = HuffNode(None, self.root, huffTree.root)
        self.weight += huffTree.weight

    def buildTable(self):
        """
        Creates a dictionary table that matches each
        character in the tree with its corresponding bit
        sequence traversal through the tree.
        """
        table = {}
        HuffTree.buildTableHelper(self.root, "", table)
        return table
        
    #Private Recursive Method
    def buildTableHelper(node, bitSeq, table):
        """
        Only to be called by the buildTable method.
        Recursively traverses the tree to find the bit
        sequence for each character node.
        """

        #Node contains a character, add it to the table
        if (node.char):
            table[node.char] = bitSeq

        #Traverse the left side, adding a 0 to the bit sequence
        if (node.leftChild):
            left = bitSeq + '0'
            HuffTree.buildTableHelper(node.leftChild, left, table)

        #Traverse the right side, adding a 1 to the bit sequence
        if (node.rightChild):
            right = bitSeq + '1'
            HuffTree.buildTableHelper(node.rightChild, right, table)

    def decodeBits(self, bitStream):
        """
        Uses the bit stream to traverse the tree. When a
        leaf node is reached, the character contained in the
        node is recorded, and the traversal begins again at
        the root node.
        """
        decodedData = ""     #Stores decompressed message
        currNode = self.root #Starting point

        for bit in bitStream:

            #Navigate the tree
            if (bit == "0"):
                currNode = currNode.leftChild
            else:
                currNode = currNode.rightChild

            #Check if leaf Node - node should have both or no children
            if not currNode.rightChild:

                #Check if end of compressed data is reached
                if (currNode.char == HuffTree.eof):
                    return decodedData
                
                decodedData += currNode.char    #Record character
                currNode = self.root            #Start over

        #Occurs if no eof character is reached
        print ("ERROR:  No EOF character found.")
        return 0

    def storeData(self, bitStream):
        """
        After dumping the serialized huffman tree into a file
        using Python's pickle module, the file can only be
        further written to with "bytes-like" objects. Thus,
        we store the compressed data with the tree so that it can
        be stored in the compressed file and retrieved along
        with the tree.
        """
        self.storedData = bitStream
        
        
def getInput(filename):
    """
    Reads the given file to collect data to compress
    """
    try:
        source = open(filename)
        data = source.read()
        source.close()
    except FileNotFoundError:
        data = "Your input file didn't exist!"
    return data

def calcCharWeights(sourceData):
    """
    Reads character-by-character through the sourceData String
    and records the occurrences of each character.
    """
    charWeights = {}
    for line in sourceData:
        for char in line:
            if char not in charWeights:
                #Add new character to dictionary with count of 1
                charWeights[char] = 1
            else:
                #Increase count of character by 1
                charWeights[char] += 1
                
    #Include EOF character
    #TODO Check if this is allowed, as it is more than 1 char.
    charWeights[HuffTree.eof] = 1
    
    return charWeights

def buildForest(charWeights):
    """
    Creates a HuffTree of length one for each character in
    charWeight dictionary and stores the HuffTrees in a list.
    """
    forest = []
    for char in charWeights:
        charNode = HuffNode(char)
        weight = charWeights[char]
        forest.append(HuffTree(weight, charNode))
    return forest

def buildHuffTree(forest):
    """
    Combines all of the HuffTrees of length one into a
    single HuffTree.
    """
    while (len(forest) > 1):
        #Get tree with smallest weight
        smallest = min(forest, key=lambda tree: tree.weight)

        #Pull it out of the forest
        forest.remove(smallest)

        #Get next smallest tree
        othersmall = min(forest, key=lambda tree: tree.weight)

        #Combine smallest tree into next smallest
        othersmall.addHuffTree(smallest)

    return forest[0]

def compress(sourceFile, destinationFile):
    """
    Takes data from the given source file and compresses it with
    huffman encoding.  The compressed data, along with the
    serialized huffman tree, is stored in the given file.
    """

    #Get uncompressed data from file
    sourceData = getInput(sourceFile)

    #Create a huffman tree
    charWeights = calcCharWeights(sourceData)
    forest = buildForest(charWeights)
    huffmanTree = buildHuffTree(forest)
    
    #Make the character table to convert characters into bits
    charTable = huffmanTree.buildTable()
    
    #Convert characters from data into compressed form
    output = ""
    for line in sourceData:
        for char in line:
            bits = charTable[char]
            output += bits

    #Add the End Of File character to the compressed data
    output += charTable[HuffTree.eof]

    #Store the compressed data with the tree for serialization
    huffmanTree.storeData(output)

    #Dump the huffman tree into the given destination file
    destination = open(destinationFile, mode='wb')
    dump = pickle.dump(huffmanTree, destination)
    destination.close()

def decompress(compressedFile, destinationFile):
    """
    Loads the serialized huffman tree from the compressed file and
    uncompresses the data, and writes the data to the given
    destination.
    """
    output = ""

    #Open the given files
    source = open(compressedFile, mode='rb')
    destination = open(destinationFile, 'w')

    #Load in the serialized huffman tree
    huffmanTree = pickle.load(source)

    #Decompress the data
    bitStream = huffmanTree.storedData
    data = huffmanTree.decodeBits(bitStream)

    #Write the decompressed data to the destination
    destination.write(data)

    #Close the given files
    source.close()
    destination.close()
    
                
def main():
    
    inputData = "HuffmanInput.txt"          #File with original input
    compressedData = "Compressed.txt"       #File with compressed data
    uncompressedData = "Uncompressed.txt"   #File with data after decompression

    #Compress and store the data
    compress(inputData, compressedData)

    #Decompress the data
    decompress(compressedData, uncompressedData)


main()
