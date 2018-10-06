import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class District {

    private final String name;
    private final Poll[] polls;
    private final boolean repIncumbent;
    private final boolean demIncumbent;
    private final double obama2012;
    private final Double dem2014;
    private final double hillary2016;
    private final Double dem2016;
    private final double elasticity;
    private final Double blairvoyanceDemPercent;
    private final boolean contested;

    private double fundamentalDemPercent;
    private double fundamentalStDv;
    private double genericCorrectedDemPercent;
    private double genericCorrectedStDv;
    private double finalDemPercent;
    private double finalStDv;

    private District(String name, Poll[] polls, boolean repIncumbent,
                     boolean demIncumbent, double obama2012, Double dem2014,
                     double hillary2016, Double dem2016, double elasticity,
                     Double blairvoyanceDemPercent, boolean repRunning, boolean demRunning) {
        this.name = name;
        this.polls = polls;
        this.repIncumbent = repIncumbent;
        this.demIncumbent = demIncumbent;
        this.obama2012 = obama2012;
        this.dem2014 = dem2014;
        this.hillary2016 = hillary2016;
        this.dem2016 = dem2016;
        this.elasticity = elasticity;
        this.blairvoyanceDemPercent = blairvoyanceDemPercent;
        this.contested = repRunning && demRunning;

        if (!contested) {
            this.genericCorrectedStDv = 0;
            this.finalStDv = 0;
            if (!repRunning) {
                this.genericCorrectedDemPercent = 1;
                this.finalDemPercent = 1;
            } else {
                this.genericCorrectedDemPercent = 0;
                this.finalDemPercent = 0;
            }
        }
    }

    public static District[] parseFromCSV(String districtFile, String pollFile, String blairvoyanceFile) throws IOException {
        String line;
        BufferedReader pollFileReader = new BufferedReader(new FileReader(pollFile));
        //Clear header line
        pollFileReader.readLine();
        Map<String, List<Poll>> nameToPollMap = new HashMap<>();
        while ((line = pollFileReader.readLine()) != null) {
            String[] commaSplit = line.split(",");
            String name = commaSplit[0].toUpperCase();
            LocalDate date = LocalDate.parse(commaSplit[1], DateTimeFormatter.ofPattern("M/d/yyyy"));
            double rawDemPercent = Double.parseDouble(commaSplit[2]);
            double rawRepPercent = Double.parseDouble(commaSplit[3]);
            double sampleSize = Double.parseDouble(commaSplit[4]);
            boolean registeredVoter = Boolean.parseBoolean(commaSplit[5]);
            double houseLean = Double.parseDouble(commaSplit[6]);
            Grade grade = Grade.parseGrade(commaSplit[7]);
            String pollsterName = commaSplit[8];
            Poll poll = new Poll(date, rawDemPercent, rawRepPercent, sampleSize, registeredVoter, houseLean, grade,
                    pollsterName);
            if (nameToPollMap.containsKey(name)) {
                nameToPollMap.get(name).add(poll);
            } else {
                List<Poll> pollList = new ArrayList<>();
                pollList.add(poll);
                nameToPollMap.put(name, pollList);
            }
        }
        pollFileReader.close();

        BufferedReader blairvoyanceFileReader = new BufferedReader(new FileReader(blairvoyanceFile));
        //Clear header line
        blairvoyanceFileReader.readLine();
        Map<String, Double> nameToBlairvoyanceMap = new HashMap<>();
        while ((line = blairvoyanceFileReader.readLine()) != null) {
            String[] commaSplit = line.split(",");
            String name = commaSplit[0].toUpperCase();
            nameToBlairvoyanceMap.put(name, Double.parseDouble(commaSplit[1]));
        }
        blairvoyanceFileReader.close();

        BufferedReader districtFileReader = new BufferedReader(new FileReader(districtFile));
        //Clear header line
        districtFileReader.readLine();
        List<District> toRet = new ArrayList<>();
        while ((line = districtFileReader.readLine()) != null) {
            String[] commaSplit = line.split(",");
            String name = commaSplit[0].toUpperCase();
            boolean repIncumbent = Integer.parseInt(commaSplit[1]) == 1;
            boolean demIncumbent = Integer.parseInt(commaSplit[2]) == 1;
            double obama2012 = Double.parseDouble(commaSplit[3]);

            Double dem2014 = null;
            try {
                dem2014 = Double.parseDouble(commaSplit[4]);
            } catch (NumberFormatException ignored) {
            }

            double hillary2016 = Double.parseDouble(commaSplit[5]);

            Double dem2016 = null;
            try {
                dem2016 = Double.parseDouble(commaSplit[6]);
            } catch (NumberFormatException ignored) {
            }

            double elasticity = Double.parseDouble(commaSplit[7]);

            boolean repRunning = Boolean.parseBoolean(commaSplit[8]);
            boolean demRunning = Boolean.parseBoolean(commaSplit[9]);

            Poll[] polls = null;
            if (nameToPollMap.containsKey(name)) {
                polls = nameToPollMap.get(name).toArray(new Poll[1]);
            }

            Double blairvoyanceDemPercent = null;
            if (nameToBlairvoyanceMap.containsKey(name)) {
                blairvoyanceDemPercent = nameToBlairvoyanceMap.get(name);
            }

            toRet.add(new District(name, polls, repIncumbent, demIncumbent, obama2012, dem2014,
                    hillary2016, dem2016, elasticity, blairvoyanceDemPercent, repRunning, demRunning));
        }
        districtFileReader.close();

        return toRet.toArray(new District[435]);
    }

    public String getName() {
        return name;
    }

    public Poll[] getPolls() {
        return polls;
    }

    public boolean isRepIncumbent() {
        return repIncumbent;
    }

    public boolean isDemIncumbent() {
        return demIncumbent;
    }

    public double getObama2012() {
        return obama2012;
    }

    public Double getDem2014() {
        return dem2014;
    }

    public double getHillary2016() {
        return hillary2016;
    }

    public Double getDem2016() {
        return dem2016;
    }

    public double getElasticity() {
        return elasticity;
    }

    public Double getBlairvoyanceDemPercent() {
        return blairvoyanceDemPercent;
    }

    public double getFundamentalDemPercent() {
        return fundamentalDemPercent;
    }

    public void setFundamentalDemPercent(double fundamentalDemPercent) {
        this.fundamentalDemPercent = fundamentalDemPercent;
    }

    public double getGenericCorrectedDemPercent() {
        return genericCorrectedDemPercent;
    }

    public void setGenericCorrectedDemPercent(double genericCorrectedDemPercent) {
        this.genericCorrectedDemPercent = genericCorrectedDemPercent;
    }

    public double getFinalDemPercent() {
        return finalDemPercent;
    }

    public void setFinalDemPercent(double finalDemPercent) {
        this.finalDemPercent = finalDemPercent;
    }

    public double getFundamentalStDv() {
        return fundamentalStDv;
    }

    public void setFundamentalStDv(double fundamentalStDv) {
        this.fundamentalStDv = fundamentalStDv;
    }

    public double getGenericCorrectedStDv() {
        return genericCorrectedStDv;
    }

    public void setGenericCorrectedStDv(double genericCorrectedStDv) {
        this.genericCorrectedStDv = genericCorrectedStDv;
    }

    public double getFinalStDv() {
        return finalStDv;
    }

    public void setFinalStDv(double finalStDv) {
        this.finalStDv = finalStDv;
    }

    public boolean hasPolls() {
        return polls != null && !(polls.length == 0);
    }

    public boolean isContested() {
        return contested;
    }

    @Override
    public String toString() {
        return getName() + ", polls: " + this.hasPolls() + ", dem2014: " + this.getDem2014() + ", dem2016: " + this.getDem2016()
                + ", Obama: " + this.getObama2012() + ", Hillary: " + this.getHillary2016() + ", fundamental dem %: " +
                this.getFundamentalDemPercent() + ", final dem %: " + this.getFinalDemPercent();
    }
}
