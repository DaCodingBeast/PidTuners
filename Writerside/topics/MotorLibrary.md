# Motor Library: FTC Motor Specifications


## Overview

As part of our project, we conducted extensive research to compile accurate specifications for all FTC-permissible motors. This database serves as the foundation for our simulation engine, ensuring realistic motor behavior in our PID tuning process.

## Motor Specifications Database

Our research included:
- Manufacturer specifications
- Real-world testing
- Gear ratio calculations
- Encoder resolution verification

Here's a sample of the specifications we've compiled:

| Motor Type | Free Speed (RPM) | Stall Torque | Gear Ratio | Encoder Ticks/Revolution |
|------------|------------------|--------------|------------|--------------------------|
| GoBILDA 5201 | 340 | 1.2357 oz-in | 19.2:1 | 537.6 |
| GoBILDA 5202 | 160 | 2.47 oz-in | 40:1 | 1120 |
| GoBILDA 5203 | 105 | 3.707 oz-in | 60:1 | 1680 |
| REV HD Hex (3:1) | 2000 | 0.315 Nm | 3:1 | 28 |
| REV HD Hex (4:1) | 1500 | 0.42 Nm | 4:1 | 28 |
| REV HD Hex (5:1) | 1200 | 0.525 Nm | 5:1 | 28 |
| REV HD Hex (12:1) | 500 | 1.26 Nm | 12:1 | 28 |
| REV HD Hex (20:1) | 300 | 2.1 Nm | 20:1 | 28 |

## Implementation Details

The motor specifications are implemented in our codebase as a comprehensive database. For example, the HD Hex motors are implemented with a flexible gear ratio system:

```kotlin
enum class HDHexGearRatios(val value: Double) {
    GR3_1(3.0),
    GR4_1(4.0),
    GR5_1(5.0)
}
```

This allows for accurate simulation of any HD Hex motor configuration by combining different gear ratios. For instance, a 20:1 HD Hex motor is simulated by combining 5:1 and 4:1 gear ratios.

## Research Methodology

To ensure accuracy, we:
1. Verified specifications against manufacturer documentation
2. Cross-referenced multiple sources
3. Validated gear ratio calculations
4. Confirmed encoder resolutions
5. Tested torque and speed characteristics

## Further Reading
- [Arm Mechanism Documentation](ArmMechanism.md)

[//]: # (- [Simulation Engine Documentation]&#40;SimulationEngine.md&#41; )