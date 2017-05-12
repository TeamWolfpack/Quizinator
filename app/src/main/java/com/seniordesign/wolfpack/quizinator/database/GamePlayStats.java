package com.seniordesign.wolfpack.quizinator.database;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents game play stats
 * @creation 10/14/2016.
 */
public class GamePlayStats implements Parcelable {
    private int score;
    private long timeElapsed;
    private int totalCardsCompleted;
    private long deckID;

    public GamePlayStats(){

    }

    /*
     * @author kuczynskij (10/14/2016)
     */
    public GamePlayStats(Parcel in) {
        score = in.readInt();
        timeElapsed = in.readLong();
        totalCardsCompleted = in.readInt();
        deckID = in.readLong();
    }

    /*
     * @url http://www.parcelabler.com/
     * @author kuczynskij (10/14/2016)
     */
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<GamePlayStats> CREATOR = new Parcelable.Creator<GamePlayStats>() {
        @Override
        public GamePlayStats createFromParcel(Parcel in) {
            return new GamePlayStats(in);
        }

        @Override
        public GamePlayStats[] newArray(int size) {
            return new GamePlayStats[size];
        }
    };

    /*
     * @author kuczynskij (10/14/2016)
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /*
     * @author kuczynskij (10/14/2016)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(score);
        dest.writeLong(timeElapsed);
        dest.writeInt(totalCardsCompleted);
        dest.writeLong(deckID);
    }

    /*
     * @author kuczynskij (10/14/2016)
     */
    public int getTotalCardsCompleted() {
        return totalCardsCompleted;
    }

    /*
     * @author kuczynskij (10/14/2016)
     */
    public void setTotalCardsCompleted(int totalCardsCompleted) {
        this.totalCardsCompleted = totalCardsCompleted;
    }

    /*
     * @author kuczynskij (10/14/2016)
     */
    public long getTimeElapsed() {
        return timeElapsed;
    }

    /*
     * @author kuczynskij (10/14/2016)
     */
    public void setTimeElapsed(long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    /*
     * @author kuczynskij (10/14/2016)
     */
    public int getScore() {
        return score;
    }

    /*
     * @author kuczynskij (10/14/2016)
     */
    public void setScore(int score) {
        this.score = score;
    }

    public long getDeckID(){return deckID;}

    public void setDeckID(long deckID){ this.deckID = deckID;}
}
