# FTC-Drive-Mode
A collection of FTC java programs for controlling a simple 2 motor robot with different drivemodes using the Ps4 controller API.

---

***Presented by Team 27598***

---

### Structure
The files are split into three folders: `Single`, `CoOp` and `BattleBot`.    
The programs in `Single` and `CoOp` are meant for an obstacle course    
As the name implies the programs in `BattleBot` is meant for, well, battlebots.

> [!Note]
> When hardwaremapping the touch sensors, they should go into the `digital devices` section   
> More importantly, the hardwaremap port should be one greater than the physical port on the control hub   
> For example: the color sensor is plugged into the port on the control hub labeled as `2:3`, then the sensor should be hardwaremaped to port 3

> [!Warning]
> All servos used on the robots should not be continuos

---

#### `Single`
| Name | Features |
| --- | --- |
| `HardOpMode_Linear` | Most basic one, joysticks to move and bumpers to increase and decrease the Gowtham Speed |
| `EasyOpMode_Linear` | A slightly "easier" to control robot, where the Gowtham Speed in plugged into the Sine function. Increase the `power` to make the cycle faster | 
| `UltraEasyOpMode` | The most advanced "algorithim" developed yet. 8 different controls, fully utilizing EVERY button on the Ps4 controller. Two events that will activate once the color sensor on the robot detects green. The robot controls will be reversed for three seconds after the color sensor detects red. As well as a servo functioning as a hammer for some extra fun. |

#### `CoOp` 
| Name | Features |
| --- | --- |
| `HardDualWielding` | The basic one, designed for two players, each player uses triggers to control the left wheel and right wheel |
| `EasyDualWielding` | An improved driver "assisstance" compared to the Harder version, has seven different control options, randomized every 30 seconds, follows the logic of the hard version, each player controls one motor | 

---

#### `BattleBot`
| Name | Features |
| --- | --- |
| `GameOP_Hammer` | Used on a robot with one servo functioning as a hammer |
| `GameOP_Ham_Sweep` | Used on a robot with one servo as a hammer and another as a sweeper swinging left and right
| `GameOP_DoubleSweeper` | One of the rare cases where there are two sweeper on a single robot |

--- 

It is strongly recommended to use [Sloth](https://github.com/Dairy-Foundation/Sloth) for fastsloading to speed up development as you always need to configure the servos in the right direction
