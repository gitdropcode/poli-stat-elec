import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        Map<Grade, Double> gradeQualityPoints = new HashMap<>();
        gradeQualityPoints.put(Grade.A, 0.176981753181247);
        gradeQualityPoints.put(Grade.B, 0.150950231708606);
        gradeQualityPoints.put(Grade.C, 0.130417204637636);
        gradeQualityPoints.put(Grade.D, 0.076745970836531);

        FundamentalCalculator fundamentalCalculator = new LinearFundamentalCalculator(0.133,
                0.278,0.244,0.345, 1.1645,
                0.1558,-0.1405, 0.1, 0.15);
        NationalShiftCalculator natlShiftCalc = new SimpleNatlShiftCalc("2014.csv");
        District[] districts = District.parseFromCSV("district_input.csv", "poll_input.csv",
                "bantor_input.csv");
        fundamentalCalculator.calcAll(districts);
//        for (District district : districts){
//            System.out.println(district.getFundamentalDemPercent());
//        }

        double[] results = new double[436];
        Random generator = new Random();
        for (int i = 0; i < 1000; i++) {
//            double nationalShift = natlShiftCalc.calcNationalShift(districts, (0.493/(0.493+0.413) + generator.nextGaussian()*0.0));
//            System.out.println("Shift: "+nationalShift);
            NationalCorrectionCalculator natlCorrectCalc = new NationalShift(0.05 + generator.nextGaussian()*0.01);
            natlCorrectCalc.calcAll(districts);
            PollAverager pollAverager = new ExponentialPollAverager(1. / 30.);
            PollCalculator pollCalculator = new ArctanPollCalculator(pollAverager, gradeQualityPoints, 1. / 167.,
                    0.9, 0, 16.6, 0.0, 0.05);
            pollCalculator.calcAll(districts);
            double[] result = Simulations.write(districts, 1000);
            for (int j = 0; j < 436; j++){
                results[j] += result[j];
            }
        }
//        System.out.println(Arrays.toString(results));
        double totalDemProb = 0;
        for (int i = 0; i < results.length; i++) {
            System.out.println(i + ": "+results[i]);
            if (i >= 218) {
                totalDemProb += results[i];
            }
        }
        System.out.println(Arrays.toString(results));
        System.out.println("Total win %: "+totalDemProb/Math.pow(100., 2));
    }
}
