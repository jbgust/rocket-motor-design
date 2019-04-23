package com.rocketmotordesign.propellant;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class BurnRateCoefficientConverter {

    private static Expression toMetriqueFormulas = new ExpressionBuilder("a * 25.4 * (1000000/6895)^n")
            .variables("a", "n")
            .build();

    public static double toMetrique(double burnRateCoefficient, double burnRateExponent) {
        return toMetriqueFormulas
                .setVariable("a", burnRateCoefficient)
                .setVariable("n", burnRateExponent)
                .evaluate();
    }
}
