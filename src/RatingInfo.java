class RatingInfo {
    private final long userID;
    private final long itemID;
    private final int rating;

    public RatingInfo(long userID, long itemID, int rating) {
        this.userID = userID;
        this.itemID = itemID;
        this.rating = rating;
    }

    public long getUserID() {
        return userID;
    }

    public long getItemID() {
        return itemID;
    }

    public int getRating() {
        return rating;
    }
}
