# blockChainVoting
Voting on machines running blockchains. Blockchain verification is such that block is only accepted if 90%(GLOBAL VARIABLE) of the blocks are all the same, and a changed block would be rewritten.
Three priority rankings can be selected.

There can be three main parts to the program - 
1.  user enters voting id number(unless the card is scannable) and candidate number(or clicks on their picture/whatever). then {<voting id number>, <priority>} is the asset of candidate number {<candidate number>}. This is written to a block of 500(GLOBAL VARIABLE) entries. At the end, winner can be calculated by adding all priorities together. figure out how to accomodate votes coming in from different machines together.
2.  take all blocks and parse file to calculate the total votes(every 30 min?). if a block doesn't agree with the same block from other machines (90%, global var), replace all the blocks from machines with different blocks. if more than 10% of the machines return a different block, quit and say that the vote was rigged.
3.  UI and network. Application that can access the harddrive(where the blocks are stored). How to communicate between computers on the internet. 
  
  
