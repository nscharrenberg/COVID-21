package org.um.nine.utils.cardmanaging;
import org.um.nine.domain.Card;
import org.um.nine.domain.cards.EpidemicCard;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

public class Shuffle {

    /**
     * Shuffles the deck and then splitting the deck in the amount of difficulty.
     * Those get a epidemic card added and then shuffled again.
     * @return a shuffled deck of cards including difficulty*epidemic cards mixed in every quarter
     */
    public Stack<Card> difficultyShuffle(int difficulty, Stack<Card> deck){
        deck = shuffle(deck);
        Stack<Card> shuffled = new Stack<>();

        Stack<Card>[] split = new Stack[difficulty];
        for(int i = 0; i < split.length; i++){
            split[i] = new Stack<>();
        }

        int counter = 0;
        for (Card c: deck) {
            if(counter > 3) counter = 0;
            split[counter].add(c);
            counter++;
        }


        for(int i = 0; i < split.length; i++){
            split[i].add(new EpidemicCard("Epidemic!"));
            split[i] = shuffle(split[i]);
        }

        for(int i = 0; i < split.length; i++){
            for (Card c: split[i]) {
                shuffled.add(c);
            }
        }

        return shuffled;
    }

    public Stack<Card> shuffle(Stack<Card> deck){
        LinkedList<Card> list = new LinkedList<>();
        for(int i = 0; i < deck.size(); i++){
            list.add(deck.elementAt(i));
        }
        Stack<Card> newDeck = new Stack<>();
        while(!list.isEmpty()){
            int index = (int) Math.round(Math.random() * (list.size()-1));
            newDeck.add(list.remove(index));
        }
        return newDeck;
    }

}
