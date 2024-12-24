package ArmSpecific

/**
 * A collection of Motor Brands and characteristics
 */
sealed class Hardware {
    /**
     * Yellow Jacket Motors ranging from 84 - 1150 RPM
     */
    object YellowJacket {
        @JvmField
        val RPM30 = Motor(
            30.0,
            ((((((1 + (46 / 17))) * (1 + (46 / 17))) * (1 + (46 / 17))) * (1 + (46 / 17))) * 28).toDouble(),
            250.0
        )

        @JvmField
        val RPM43 = Motor(
            43.0,
            ((((1.0 + (46.0 / 11.0))) * (1.0 + (46.0 / 11.0))) * (1.0 + (46.0 / 11.0)) * 28.0),
            185.0
        )

        @JvmField
        val RPM60 = Motor(
            60.0,
            ((((1.0 + (46.0 / 17.0))) * (1.0 + (46.0 / 11.0))) * (1.0 + (46.0 / 11.0)) * 28.0),
            133.2
        )

        @JvmField
        val RPM84 = Motor(
            84.0,
            ((((1.0 + (46.0 / 17.0))) * (1.0 + (46.0 / 17.0))) * (1.0 + (46.0 / 11.0)) * 28.0),
            93.6
        )

        @JvmField
        val RPM117 = Motor(
            117.0,
            ((((1.0 + (46.0 / 17.0))) * (1.0 + (46.0 / 17.0))) * (1.0 + (46.0 / 17.0)) * 28.0),
            68.4
        )

        @JvmField
        val RPM223 = Motor(223.0, ((((1 + (46 / 11))) * (1 + (46 / 11))) * 28).toDouble(), 38.0)

        @JvmField
        val RPM312 = Motor(312.0, ((((1 + (46 / 17))) * (1 + (46 / 11))) * 28).toDouble(), 24.3)

        @JvmField
        val RPM435 = Motor(435.0, ((((1 + (46 / 17))) * (1 + (46 / 17))) * 28).toDouble(), 18.7)

        @JvmField
        val RPM1150 = Motor(1150.0, ((1 + (46 / 11)) * 28).toDouble(), 7.9)

        @JvmField
        val RPM1620 = Motor(1620.0, ((1 + (46 / 17)) * 28).toDouble(), 5.4)

        @JvmField
        val RPM6000 = Motor(6000.0, 28.0, 1.47)
    }

    /**
     * Holds the necessary specs needed for this simulation, all which can be found on the vendor's website
     * @param rpm Theoretical rpm
     * @param stallTorque The motors maximum Torque in Kg.cm
     * @param customGearRatio Any gear conversions that need to be considered
     * Gear ratio is in the form of a fraction: (Motor gear teeth) / (Arm Gear Teeth)
     */

    data class Motor(
        var rpm: Double,
        var encoderTicksPerRotation: Double,
        var stallTorque: Double,
        var customGearRatio: Double = 1.0
    ) {
        init {
            if (customGearRatio != 0.0) {
                rpm *= customGearRatio
                encoderTicksPerRotation *= (1 / customGearRatio)
                stallTorque *= (1 / customGearRatio)
            }
            else {
            //todo log
            }
        }
    }
}
