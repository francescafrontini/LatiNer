import re
import collections

#suffixes = ["ibus","ius","ae","am","as","em","es","ia","is","in","nt","os","ud","um","us","im","a","e","i","o","u"]

#simplified stemmer
suffixes = ["ae","am","as","em","es","is","os","on","en","in","um","us","im","a","e","i","o","u"]



def stemmer_word(word):
	for suffix in suffixes:
		l = len(suffix)
		if word.endswith(suffix):
    			return word[:-l]
	return word
	

def stemmer_string(string):
	words = string.split(" ")
	result = list()
	for word in words:
		result.append( stemmer_word(word) )
	return " ".join(result)

def is_ascii(s):
    return all(ord(c) < 128 for c in s)


index_pleiades = collections.defaultdict(set)


f = open('/home/frontini/Documents/ammianus/pleiades/pleiades_names_simplified.csv')

for line in f:
	line = line.strip()
	name = line.split("\t")[1]
	link = line.split("\t")[2]

	#print name, link

	index_pleiades[stemmer_string(name)].add(link)

f.close()

#UNCOMMENT HERE TO print pleiades index

#for i in index_pleiades:
#	links = "\t".join(index_pleiades[i])
#	print i + "\t" + links 



#UNCOMMENT HERE TO print tag dictionary for DictionaryNamefinder

print "<dictionary>"
for i in index_pleiades:
	if is_ascii(i):
		tokens = i.split(" ")
		tokenres = ""

		for token in tokens:
			if not("." in token) and not(token==""):
				tokenres += "<token>" + token + "</token>" + "\n"
		if tokenres != "":
			print "<entry tags=\"location\">"
			print tokenres,
			print "</entry>"

print "</dictionary>"


