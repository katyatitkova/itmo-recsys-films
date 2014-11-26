import java.util.Arrays;
import java.util.List;
import java.util.Map;

class Learning {
    private double mu;
    private final double lambda;
    private final Map<Long, Integer> users;
    private final Map<Long, Integer> items;
    private final List<RatingInfo> trains;
    private final int fnum;
    private double g = 0.001;
    private final double[] bu;
    private final double[] bi;
    private final double[][] uf;
    private final double[][] itf;

    public Learning(double mu, double lambda, int fnum, Map<Long, Integer> users,
                    Map<Long, Integer> items, List<RatingInfo> trains) {
        this.lambda = lambda;
        this.fnum = fnum;
        this.mu = mu;
        this.users = users;
        this.items = items;
        this.trains = trains;
        this.bu = new double[users.size()];
        this.bi = new double[items.size()];
        this.uf = new double[users.size()][fnum];
        this.itf = new double[items.size()][fnum];
    }

    public void count() {
        double error = countErrorFunction(trains, true);
        double oldError = 0;
        double eps = 1e-6;
        while (Math.abs(error - oldError) >= eps) {
            oldError = error;
            for (RatingInfo info : trains) {
                int u = users.get(info.getUserID());
                int i = items.get(info.getItemID());
                double[] user = uf[u];
                double[] item = itf[i];
                double predictedRate = this.mu + bu[u] + bi[i] + dotProduct(user, item);
                double eui = info.getRating() - predictedRate;
                this.mu += eui * g;
                double l = 0.025;
                bu[u] += g * (eui - l * bu[u]);
                bi[i] += g * (eui - l * bi[i]);
                for (int j = 0; j < fnum; j++) {
                    double t = user[j];
                    user[j] += g * (eui * item[j] - l * user[j]);
                    item[j] += g * (eui * t - l * item[j]);
                }
            }
            error = countErrorFunction(trains, true);
            g *= 0.95;
            System.out.println(Math.abs(error - oldError));
        }
    }

    double countErrorFunction(List<RatingInfo> ratings, boolean f) {
        double error = 0;
        for (RatingInfo info : ratings) {
            double predictedRate = this.mu;
            double bu = 0;
            double bi = 0;
            double uSum = 0;
            double iSum = 0;
            double[] user = null;
            double[] item = null;
            boolean hasUser = false;
            boolean hasItem = false;
            if (users.containsKey(info.getUserID())) {
                hasUser = true;
                int u = users.get(info.getUserID());
                bu = this.bu[u] * this.bu[u];
                user = uf[u];
                uSum = Arrays.stream(user).map(b -> b * b).sum();
                predictedRate += this.bu[u];
            }
            if (items.containsKey(info.getItemID())) {
                hasItem = true;
                int i = items.get(info.getItemID());
                bi = this.bi[i] * this.bi[i];
                item = itf[i];
                iSum = Arrays.stream(item).map(b -> b * b).sum();
                predictedRate += this.bi[i];
            }
            if (hasUser && hasItem) {
                predictedRate += dotProduct(user, item);
            }
            if (f) {
                error += lambda * (bi + bu + uSum + iSum);
            }
            error += Math.pow(info.getRating() - predictedRate, 2.);
        }
        if (f) {
            return error;
        }
        return Math.sqrt(error / ratings.size());
    }

    public long predictRating(long userID, long itemID) {
        double predictedRate = this.mu;
        double[] user = null, item = null;
        boolean hasUser = false, hasItem = false;
        if (users.containsKey(userID)) {
            hasUser = true;
            user = uf[users.get(userID)];
            predictedRate += bu[users.get(userID)];
        }
        if (items.containsKey(itemID)) {
            hasItem = true;
            item = itf[items.get(itemID)];
            predictedRate += bi[items.get(itemID)];
        }
        if (hasUser && hasItem) {
            predictedRate += dotProduct(user, item);
        }
        long r = Math.round(predictedRate);
        if (r < 1) {
            r = 1;
        }
        if (r > 5) {
            r = 5;
        }
        return r;
    }

    private double dotProduct(double[] user, double[] item) {
        double res = 0.0;
        for (int i = 0; i < user.length; ++i) {
            res += user[i] * item[i];
        }
        return res;
    }
}