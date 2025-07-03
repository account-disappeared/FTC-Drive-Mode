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
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.Optional;
import java.util.Random;
import com.qualcomm.robotcore.util.Range;


@TeleOp(name="Easiest OpMode in Existence", group="Linear OpMode")

public class EasiestMode extends LinearOpMode {
    Random random = new Random();

    // Declare OpMode members.
    public static final double GAMETIME = 120;

    private double gowtham_speed = 0.6;

    private double hammerpos = .5;
    private final double HAMMER_MAX = 1; //todo DO NOT SEt tthis To 000000 !!!!!!
    private final double HAMMER_MIN = 0.5; //todo DO NOT set this TO 11111!!!!!!!
    private static final int DISABLED_TIME = 5;

    private static int shields = 0;

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;
    private Servo hammer = null;

    private TouchSensor touch_l;
    private TouchSensor touch_r;
    private boolean disabled = false;

    int spin_time;

    //disabled time

    private int shield = 0;
    private double disabled_until = 0;

    private static final String[] DRIVEMODE = {"default", "switchjoysticks", "d-pad", "buttons", "touchpad"};
    private static final String[] EVENTS = {"spin", "hammer"};

    private static final String LOCKDOWN = "lockdown";





    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        leftDrive = hardwareMap.get(DcMotor.class, "left");
        rightDrive = hardwareMap.get(DcMotor.class, "right");
        hammer = hardwareMap.get(Servo.class, "hammer");
        touch_r = hardwareMap.get(TouchSensor.class, "touch_r");
        touch_l = hardwareMap.get(TouchSensor.class, "touch_l");
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
        int eventtime = 15;
        int randome_time = 10;
        int rng_num = 8;
        while (runtime.seconds() <= 300 && opModeIsActive()) {
            if ((touch_r.isPressed() || touch_l.isPressed()) && !disabled) {
                if (shield > 0) {
                    shield --;
                }
                else {
                    disabled_until = (runtime.seconds() + DISABLED_TIME);
                }
            }
            disabled = (disabled_until > runtime.seconds());

            while (!disabled) {
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
                }

                rightPower = (drive + turn) * gowtham_speed;
                leftPower = (drive - turn) * gowtham_speed;

                if (runtime.seconds() > eventtime) {
                    eventtime = (int)(runtime.seconds() + 15);
                    events = EVENTS[random.nextInt(EVENTS.length)];
                }

                if (events.equals("spin")) {
                    spin_time = (int) (runtime.seconds() + 5);
                    if (spin_time > runtime.seconds()) {
                        leftPower = -0.4;
                        rightPower = 0.4;
                    }
                }
                else if (events.equals("hammer")) {
                    spin_time = (int)(runtime.seconds() + 5);
                    if (spin_time > runtime.seconds()) {
                        hammerpos += 0.1;
                        hammerpos -= 0.1;
                    }
                }

                if (randome_time < runtime.seconds()) {
                    rng_num = random.nextInt(10);
                    randome_time += runtime.seconds();
                }
                if (rng_num == 0) {
                    spin_time = (int) (runtime.seconds() +10);
                    disabled = true;
                }

                leftDrive.setPower(leftPower);
                rightDrive.setPower(rightPower);


                if (gamepad1.right_bumper) {
                    hammer.setPosition(min(hammer.getPosition() + 0.01, HAMMER_MAX));
                }
                else if (gamepad1.left_bumper) {
                    hammer.setPosition(max(hammer.getPosition() - 0.01, HAMMER_MIN));
                }
            }
            // Show the elapsed game time and wheel power.
            telemetry.addData("Run Time", runtime.seconds());
            telemetry.addData("Gowtham Speed", gowtham_speed);
            telemetry.addData("Current DriveMode: ", drivemode);
            telemetry.addData("Servo Position", hammer.getPosition());
            telemetry.addData("Disabled? ", disabled);
            telemetry.update();
        }
    }
}
