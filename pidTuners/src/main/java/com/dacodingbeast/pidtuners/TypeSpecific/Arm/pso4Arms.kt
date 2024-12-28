package ArmSpecific

import com.dacodingbeast.pidtuners.TypeSpecific.Arm.AngleRange
import com.dacodingbeast.pidtuners.Algorithm.PSO_Optimizer
import android.util.Log
import com.dacodingbeast.pidtuners.TypeSpecific.Arm.PivotSystemConstants
import com.dacodingbeast.pidtuners.Algorithm.Ranges
import com.dacodingbeast.pidtuners.Simulators.SimulatorType
import com.dacodingbeast.pidtuners.Simulators.Target


const val error = "ERROR_IN_CONSTANTS"
/**
 * @The Class used to run the PSO simulations and return the PID Constants
 * @param systemConstants Provide the Simulations Constants
 * @param angleRanges Provide the ArrayList of Angle Ranges you want the system to travel
 * @param time The Time provided for the simulation to reach the target. The higher it is, the better the performance
 * @param obstacle Provide any physical obstacles the system may face
 */
class pso4Arms(
    systemConstants: PivotSystemConstants,
    private val angleRanges: ArrayList<AngleRange>,
    private val time: Double,
    private val obstacle: AngleRange? = null, private val accuracy: Double = 3.5
) {
    /**
     * The Arm mechanism's characteristics
     */
    companion object System {
        lateinit var SystemConstants: PivotSystemConstants
    }

    val OneTest = angleRanges.size == 1

    init {
        SystemConstants = systemConstants
        if (obstacle != null) {
            angleRanges.forEach { angle ->
                //inverse because we are checking if the angle is in the obstacle range
                require(
                    !AngleRange.inRange(
                        obstacle,
                        angle
                    )
                ) { Log.d(error,"Your target angle range $angle is inside of your obstacle range $obstacle") }
            }
        }
    }

    /**
     *Prints PIDF Constants for each provided Angle Range
     */
    fun getPIDFConstants() {
        /**
         * Find the PIDF Constants for one Angle Target
         */
        if (OneTest) {
            val psoSimulator = PSO_Optimizer(arrayListOf(
                Ranges(0.0, accuracy),
                Ranges(0.0, accuracy/3.5),
                Ranges(0.0, accuracy),
                Ranges(0.0, accuracy)
            ),
                SimulatorType.ArmSimulator,
                time,
                angleRanges[0],
                obstacle!!
            )
            psoSimulator.update(25)
            println("(${psoSimulator.getBest()})")
        }
        /**
         * Find the PIDF Constants for multiple Angle Targets
         * @return Prints a switch case statement in the form of a string.
         * In code, this will change your PIDF constants based on the Angle Target
         * @see AngleRange
         */
        else {

            val code =
                StringBuilder("public static ArrayList<PIDFParams> params = new ArrayList<>(Arrays.asList(")


            for (i in 0 until angleRanges.size) {
                val psoSimulator = PSO_Optimizer(
                    arrayListOf(
                        Ranges(0.0, accuracy),
                        Ranges(0.0, accuracy/2),
                        Ranges(0.0, accuracy),
                        Ranges(0.0, accuracy)
                    ),
                    SimulatorType.ArmSimulator,
                    time,
                    angleRanges[i],
                    obstacle!!
                )

                psoSimulator.update(25)
                code.append("\n new PIDFParams(${psoSimulator.getBest()}),")
            }

            code.deleteAt(code.lastIndex)
            code.append("\n));")
            println(code)
        }

    }

}