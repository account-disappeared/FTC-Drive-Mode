/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import static java.lang.Math.*;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.Optional;
import java.util.Random;
import com.qualcomm.robotcore.util.Range;


@TeleOp(name="UltraEasy: Linear OpMode", group="Linear OpMode")

public class UltraEasyOpMode extends LinearOpMode {
    Random random = new Random();

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;

    private ColorSensor colorSensor = null;

    private Servo hammer = null;

    private double time_remaining = 0;

    private double gowtham_speed = 0.7;

    private double spin_time = 0;

    //hammer attack
    private double hammer_max = .9;

    //hammer retract
    private double hammer_min = 0.45;

    private static final String[] DRIVEMODE = {"default", "switchjoysticks", "triggers", "d-pad", "buttons", "touchpad", "ps", "pressjoysticks"};
    private static final String[] EVENTS = {"spin", "hammer"};


    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();


        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftDrive = hardwareMap.get(DcMotor.class, "left");
        rightDrive = hardwareMap.get(DcMotor.class, "right");
        colorSensor = hardwareMap.get(ColorSensor.class, "color");
        hammer = hardwareMap.get(Servo.class, "hammer");

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // Pushing the left stick forward MUST make robot go forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        rightDrive.setDirection(DcMotor.Direction.REVERSE);
        leftDrive.setDirection(DcMotor.Direction.FORWARD);

        // Wait for the game to start (driver presses START)
        waitForStart();
        runtime.reset();
        //hammer.setPosition(0.5);
        int counter = 0;
        boolean canDrive = true;
        String drivemode = "default";
        String events = "";
        int switchtime = 30;
        while (runtime.seconds() <= 150 && opModeIsActive()) {
            if (runtime.seconds() > switchtime) {
                switchtime = (int) (runtime.seconds() + 30);
                drivemode = DRIVEMODE[random.nextInt(DRIVEMODE.length)];
            }

            // Setup a variable for each drive wheel to save power level for telemetry
            double leftPower = 0;
            double rightPower = 0;

            double drive = 0;
            double turn = 0;

            if (drivemode.equals("default")) {
                drive = gamepad1.left_stick_y;
                turn = gamepad1.right_stick_x;
            } else if (drivemode.equals("switchjoysticks")) {
                drive = gamepad1.right_stick_y;
                turn = gamepad1.left_stick_x;
            } else if (drivemode.equals("d-pad")) {
                if (gamepad1.dpad_up) {
                    drive -= 1;
                }
                if (gamepad1.dpad_down) {
                    drive += 1;
                }
                if (gamepad1.dpad_right) {
                    turn += 1;
                }
                if (gamepad1.dpad_left) {
                    turn -= 1;
                }
            } else if (drivemode.equals("buttons")) {
                if (gamepad1.triangle) {
                    drive -= 1;
                }
                if (gamepad1.cross) {
                    drive += 1;
                }
                if (gamepad1.circle) {
                    turn += 1;
                }
                if (gamepad1.square) {
                    turn -= 1;
                }
            } else if (drivemode.equals("touchpad")) {
                if (gamepad1.touchpad_finger_1) {
                    drive = -gamepad1.touchpad_finger_1_y;
                    turn = gamepad1.touchpad_finger_1_x;
                } else {
                    drive = 0;
                    turn = 0;
                }
            } else if (drivemode.equals("ps")) {
                if (gamepad1.ps) {
                    drive = -1;
                } else if (!gamepad1.ps) {
                    drive = 0.3;
                }
                if (gamepad1.options) {
                    turn += 0.5;
                }
                if (gamepad1.share) {
                    turn -= 0.5;
                }

            }

            rightPower = (drive + turn) * gowtham_speed;
            leftPower = (drive - turn) * gowtham_speed;

            if (drivemode.equals("triggers")) {
                if (gamepad1.right_bumper) {
                    rightPower += 0.3;
                }
                if (gamepad1.left_bumper) {
                    leftPower += 0.3;
                } else {
                    rightPower = -gamepad1.left_trigger;
                    leftPower = -gamepad1.right_trigger;
                }
            } else if (drivemode.equals("pressjoysticks")) {
                if (gamepad1.left_stick_button) {
                    rightPower -= 0.4;
                }
                if (gamepad1.right_stick_button) {
                    leftPower -= 0.4;
                } else {
                    rightPower += 0.3;
                    leftPower += 0.3;
                }
            }

            if (colorSensor.red() > colorSensor.blue() && colorSensor.red() > colorSensor.green()) {
                counter = 60;
            }
            if (counter > 0) {
                telemetry.addData("Statuss", "reverse you hit REDDD");
                counter--;
                leftPower = -leftPower;
                rightPower = -rightPower;
            }

            if (colorSensor.green() > 170 && colorSensor.green() > colorSensor.red() && colorSensor.green() > colorSensor.blue()) {
                spin_time = runtime.seconds() + 3.0;
            }

            if (runtime.seconds() < spin_time) {
                telemetry.addData("Status", "SpInNIg Spin SPIN spiIIn SPInnnINNnG SPin sPIn SiPn SPin");
                hammer.setPosition(hammer_min);
                leftPower = -0.4;
                rightPower = 0.4;
            }

            leftDrive.setPower(leftPower);
            rightDrive.setPower(rightPower);


            if (gamepad1.right_bumper) {
                hammer.setPosition(Math.min(hammer.getPosition() + 0.01, hammer_max));
            } else if (gamepad1.left_bumper) {
                hammer.setPosition(Math.max(hammer.getPosition() - 0.01, hammer_min));
            }

            time_remaining = (double) (150 - runtime.seconds());

            // Show the elapsed game time and wheel power.
            telemetry.addData("Run Time", runtime.seconds());
            telemetry.addData("Time Remaining", time_remaining);
            telemetry.addData("Gowtham Speed", gowtham_speed);
            telemetry.addData("Color: ", ("R " + colorSensor.red() + ", G " + colorSensor.green() + ", B " + colorSensor.blue()));
            telemetry.addData("Current DriveMode: ", drivemode);
            telemetry.addData("Servo Position", hammer.getPosition());
            telemetry.addData("Time Score", (int) ((time_remaining / 3) + 0.5));
            telemetry.update();
        }
    }
}
