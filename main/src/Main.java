import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

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
        for (District district : districts){
            System.out.println(district);
        }
        double nationalShift = natlShiftCalc.calcNationalShift(districts, (0.493/(0.493+0.413)));
        NationalCorrectionCalculator natlCorrectCalc = new NationalShift(nationalShift);
        natlCorrectCalc.calcAll(districts);
        PollAverager pollAverager = new ExponentialPollAverager(1./30.);
        PollCalculator pollCalculator = new ArctanPollCalculator(pollAverager, gradeQualityPoints, 1./120.,
                0.9, 0, 16.6, 0.3, 0.05);
        pollCalculator.calcAll(districts);
        Simulations.write(districts, 10000);
    }
}
