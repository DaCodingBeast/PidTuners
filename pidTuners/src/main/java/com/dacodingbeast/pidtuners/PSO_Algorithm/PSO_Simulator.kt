package PSO_Algorithm

import ArmSpecific.FitnessFunction
import com.dacodingbeast.pidtuners.Mathematics.AngleRange
import kotlin.random.Random.Default.nextDouble


/**
 * PSO Simulator, that updates the swarms best position, and provides it to the particles.
 * This effectively moves the swarm towards the better performing PIDF Coefficients
 * @param swarmSize The number of Particles
 * @param randomInRanges The Ranges of the PIDF Coefficients
 * @param angleRange Start and Target Angles
 * @param obstacleAngleRange Obstacle
 * @param multiplePidfs Allows the use of Multiple PIDF Controllers at once
 * @param pBestWeight The Impact of the Particles prev position on particle's new velocity
 * @param prevVeloWeight The Impact of the Particles previous velocity on particle's new velocity
 * @param gBestWeight The Impact of the Swarms best position on the particle's new velocity
 */
//decrease previous when running a ton of swarm

class Ranges(val start: Double, val stop: Double)


class PSO(
    swarmSize: Int,
    private val randomInRanges: ArrayList<Ranges>,
    time: Double,
    angleRange: AngleRange,
    obstacleAngleRange: AngleRange? = null,
    private val multiplePidfs: Boolean = false,
    private val prevVeloWeight: Double = 0.01,
    private val pBestWeight: Double = .02,
    private val gBestWeight: Double = .9
) {
    private val particles = Array(swarmSize) { Particle(randomInRanges, multiplePidfs) }

    //initialize
    private var gBestResult = Double.MAX_VALUE
    var gBestParams = Vector(randomInRanges.map { nextDouble(it.start, it.stop) }.toDoubleArray())

    /**
     * The fitness function you are using
     */
    private var fitnessFunction = FitnessFunction(time, angleRange, obstacleAngleRange)

    var lastPower = 0.0

    fun update(a: Int): Double {
        (0 until a).forEach { b ->
            for (particle in particles) {
                val holdData = particles.indexOf(particle) % (50000/1) ==0

                val fitness = fitnessFunction.computeParticle(particle)

                if(holdData) {(0 until fitness.history.size).forEach { c ->
//                        println(" motor power: ${fitness.history[c].motorPower}")
                   println("[\"$b\", ${Math.toDegrees(fitness.history[c].armAngle.start)}],")
//                            println(" error: ${Math.toDegrees(fitness.second[c].third)}")
                }}

                lastPower = fitness.history[2].motorPower
                //PBEST
                if (particle.bestResult > fitness.itae) {
                    particle.bestResult = fitness.itae
                    particle.pBestParam = particle.position
//                    if(b>0) println(" fdfdf${particle.bestResult} ${fitness.first}")
                }

                //todo change from .first etc to actual names
                //GBEST
                if (gBestResult > particle.bestResult) {
                    gBestResult = particle.bestResult
                    gBestParams = particle.pBestParam

                }

                var newVelocity = ((particle.velocity * prevVeloWeight)+
                    ((particle.pBestParam - particle.position) * pBestWeight * nextDouble())+
                    ((gBestParams - particle.pBestParam) * gBestWeight * nextDouble()))

//                println("o ${particle.position}")

                /**
                 * Make sure all params are positive
                 */

                var counter = 0
                while((particle.position+newVelocity).isNegative()){
                    counter+=1
                    newVelocity = ((particle.velocity * prevVeloWeight)+
                            ((particle.pBestParam - particle.position) * pBestWeight * nextDouble())+
                            ((gBestParams - particle.pBestParam) * gBestWeight * nextDouble()))

                    //todo weird error I have not found the answer to, so just in case, force a positive value

                    if(counter>100) newVelocity = Vector(doubleArrayOf(0.0,0.0,0.0,0.0))
//                    if((particle.position+ newVelocity).isNegative()) println("H${particle.position} j$newVelocity")
                }

                particle.velocity = newVelocity
                particle.position += particle.velocity
//                println("i ${particle.position}")

            }


        }
        return lastPower
    }
}
//only have prints be the global best at the end