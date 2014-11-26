import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class SVD {
    private List<RatingInfo> trains;
    private List<RatingInfo> validation;
    private Map<Long, Integer> users;
    private Map<Long, Integer> items;
    private Learning ans;
    private double mu;

    public SVD() throws IOException {
        trains = getData("data/train.csv");
        validation = getData("data/validation.csv");
        init();
    }

    private List<RatingInfo> getData(String filename) throws IOException {
        List<RatingInfo> ratings = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = br.readLine();
        while((line = br.readLine()) != null) {
            String[] info = line.split(",");
            RatingInfo rateInfo = new RatingInfo(
                    Long.parseLong(info[0]), Long.parseLong(info[1]), Integer.parseInt(info[2]));
            ratings.add(rateInfo);
        }
        return ratings;
    }

    private void init() {
        this.users = new HashMap<>();
        this.items = new HashMap<>();
        int sum = 0;
        for (RatingInfo info : trains) {
            if (!users.containsKey(info.getUserID())) {
                users.put(info.getUserID(), users.size());
            }
            if (!items.containsKey(info.getItemID())) {
                items.put(info.getItemID(), items.size());
            }
            sum += info.getRating();
        }
        this.mu = sum * 1. / trains.size();
    }

    public void learn() {
        double lambda = 0.093;
        int fnum = 10;
        Learning learning = new Learning(mu, lambda, fnum, users, items, trains);
        learning.count();
        double RMSE = learning.countErrorFunction(validation, false);
        trains.addAll(validation.stream().collect(Collectors.toList()));
        init();
        ans = new Learning(mu, lambda, fnum, users, items, trains);
        ans.count();
        System.out.println("RMSE = " + RMSE);
    }

    public void check() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("data/test-ids.csv"));
        PrintWriter printWriter = new PrintWriter("res.csv");
        String line = br.readLine();
        StringBuilder ans = new StringBuilder("id,rating\n");
        while ((line = br.readLine()) != null) {
            String[] test = line.split(",");
            ans.append(Integer.parseInt(test[0])).append(',').append(
                    this.ans.predictRating(Long.parseLong(test[1]), Long.parseLong(test[2]))).append('\n');
        }
        printWriter.write(ans.toString());
    }
}
