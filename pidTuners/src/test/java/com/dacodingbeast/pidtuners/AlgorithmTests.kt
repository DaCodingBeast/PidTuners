//import com.dacodingbeast.pidtuners.Algorithm.FitnessFunction
//import com.dacodingbeast.pidtuners.Algorithm.PSO_Optimizer
//import com.dacodingbeast.pidtuners.Algorithm.Particle
//import com.dacodingbeast.pidtuners.Algorithm.Ranges
//import com.dacodingbeast.pidtuners.Constants.Constants
//import com.dacodingbeast.pidtuners.Constants.GravityModelConstants
//import com.dacodingbeast.pidtuners.Constants.PivotSystemConstants
//import com.dacodingbeast.pidtuners.HardwareSetup.Hardware
//import com.dacodingbeast.pidtuners.Simulators.AngleRange
//import com.dacodingbeast.pidtuners.Simulators.SimulatorType
//import com.qualcomm.robotcore.hardware.DcMotorSimple
//import junit.framework.TestCase.assertTrue
//import org.junit.Assert.assertNotEquals
//import org.junit.Test
//
//class AlgorithmTests {
//
//    private val motor = Motor(
//        "",
//        DcMotorSimple.Direction.FORWARD,
//        Hardware.YellowJacket.RPM223,
//        1.0,
//        null,
//        null
//    )
//    private val constants = Constants(
//        motor,
//        listOf(AngleRange.fromRadians(0.0, 1.0)),
//        PivotSystemConstants(
//            1.0,
//            223.0,
//            GravityModelConstants(1.0, 1.0, 1.0)
//        ),
//        SimulatorType.ArmSimulator
//    )
//    private val parameterRanges = arrayListOf(
//        Ranges(0.0, 1.0),
//        Ranges(1.0, 2.0)
//    )
//
//    @Test
//    fun `test particle initialization within ranges`() {
//        val optimizer = PSO_Optimizer(parameterRanges, SimulatorType.ArmSimulator, 1.0, AngleRange.fromRadians(0.0, 1.0), null, constants)
//
//        optimizer.particles.forEach { particle ->
//            particle.position.particleParams.forEachIndexed { index, value ->
//                assertTrue(value in parameterRanges[index].start..parameterRanges[index].stop)
//            }
//        }
//    }
//
//    @Test
//    fun `test fitness evaluation updates best result`() {
//        val fitnessFunction = FitnessFunction(1.0, AngleRange.fromRadians(0.0, 1.0), null, SimulatorType.ArmSimulator)
//        val particle = Particle(parameterRanges, fitnessFunction)
//
//        val initialBestResult = particle.bestResult
//        particle.updateFitness()
//        assertTrue(particle.bestResult <= initialBestResult)
//    }
//
//    @Test
//    fun `test velocity and position update`() {
//        val fitnessFunction = FitnessFunction(1.0, AngleRange.fromRadians(0.0, 1.0), null, SimulatorType.ArmSimulator)
//        val particle = Particle(parameterRanges, fitnessFunction)
//
//        val initialPosition = particle.position
//        val globalBest = Particle(parameterRanges, fitnessFunction)
//        particle.updateVelocity(globalBest)
//
//        assertNotEquals(initialPosition, particle.position)
//    }
//
//    @Test
//    fun `test global best update in optimizer`() {
//        val optimizer = PSO_Optimizer(parameterRanges, SimulatorType.ArmSimulator, 1.0, AngleRange.fromRadians(0.0, 1.0), null, constants)
//        optimizer.update(1)
//
//        optimizer.particles.forEach { particle ->
//            assertTrue(optimizer.getBest().bestResult <= particle.bestResult)
//        }
//    }
//
//    @Test
//    fun `test end-to-end optimization`() {
//        val optimizer = PSO_Optimizer(parameterRanges, SimulatorType.ArmSimulator, 10.0, AngleRange.fromRadians(0.0, 1.0), null, constants)
//
//        optimizer.update(10)
//        val bestParticle = optimizer.getBest()
//
//        assertTrue(bestParticle.bestResult < Double.MAX_VALUE)
//        println("Best Particle Fitness: ${bestParticle.bestResult}")
//    }
//}
