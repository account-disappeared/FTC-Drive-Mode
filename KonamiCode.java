package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="Konami Code", group="Linear OpMode")
public class KonamiCode extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private ElapsedTime sequenceTimer = new ElapsedTime();
    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;

    private double gowtham_speed = 0.7;

    // Konami code configuration
    private static final String[] KONAMI_CODE = {"↑", "↑", "↓", "↓", "←", "→", "←", "→", "B", "A"};
    private static final String[] KONAMI_BUTTONS = {"up", "up", "down", "down", "left", "right", "left", "right", "b", "a"};
    private static final double SEQUENCE_TIMEOUT = 7.0; // seconds to complete the sequence
    private static final double INPUT_COOLDOWN = 0.30; // seconds between inputs

    private int currentStep = 0;
    private boolean codeActivated = false;
    private double lastInputTime = 0;
    private String lastPressedButton = "none";
    private boolean wrongInput = false;
    private double wrongInputTime = 0;

    // Previous button states for edge detection
    private boolean prevDpadUp = false;
    private boolean prevDpadDown = false;
    private boolean prevDpadLeft = false;
    private boolean prevDpadRight = false;
    private boolean prevB = false;
    private boolean prevA = false;
    private boolean prevRightBumper = false;
    private boolean prevLeftBumper = false;
    private boolean prevCircle = false;
    private boolean prevSquare = false;

    // Special effects
    private ElapsedTime effectTimer = new ElapsedTime();
    private String[] spinnerFrames = {"|", "/", "-", "\\"};
    private int spinnerIndex = 0;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initializing...");
        telemetry.update();

        // Initialize hardware
        leftDrive = hardwareMap.get(DcMotor.class, "left");
        rightDrive = hardwareMap.get(DcMotor.class, "right");
        rightDrive.setDirection(DcMotor.Direction.REVERSE);
        leftDrive.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData("Status", "Ready!");
        telemetry.addData("", "");
        telemetry.addData("Konami Code", "Try to enter the secret code!");
        telemetry.addData("Hint", "↑ ↑ ↓ ↓ ← → ← → B A");
        telemetry.update();

        waitForStart();
        runtime.reset();
        effectTimer.reset();

        boolean canDrive = true;

        while (opModeIsActive()) {
            // Update spinner animation
            if (effectTimer.seconds() > 0.1) {
                spinnerIndex = (spinnerIndex + 1) % spinnerFrames.length;
                effectTimer.reset();
            }

            // Handle speed adjustment
            if (gamepad1.right_bumper && !prevRightBumper) {
                gowtham_speed = Math.min(gowtham_speed + 0.1, 0.7);
                gamepad1.rumble(0, 10, 300);
            } else if (gamepad1.left_bumper && !prevLeftBumper) {
                gowtham_speed = Math.max(gowtham_speed - 0.1, 0);
                gamepad1.rumble(10, 0, 300);
            }

            // Handle driving toggle
            if (gamepad1.circle && !prevCircle) {
                canDrive = !canDrive;
                gamepad1.rumble(1, 1, canDrive ? 100 : 200);
            }

            // Reset Konami code with Square button
            if (gamepad1.square && !prevSquare) {
                resetKonamiSequence();
                wrongInput = false;
                codeActivated = false;
                gamepad1.rumble(50);
            }

            // Check for Konami code inputs
            checkKonamiCode();

            // Drive control
            if (canDrive && !codeActivated) {
                double drive = gamepad1.left_stick_y;
                double turn = gamepad1.right_stick_x;
                double rightPower = Range.clip(drive + turn, -gowtham_speed, gowtham_speed);
                double leftPower = Range.clip(drive - turn, -gowtham_speed, gowtham_speed);

                leftDrive.setPower(leftPower * 0.9);
                rightDrive.setPower(rightPower);
            } else if (codeActivated) {
                // Special behavior when Konami code is activated
                double time = runtime.seconds();
                double spinSpeed = Math.sin(time * 2) * 0.5;
                leftDrive.setPower(spinSpeed);
                rightDrive.setPower(-spinSpeed);

                // Pulsing rumble
                if ((int)(time * 2) % 2 == 0) {
                    gamepad1.rumble(100);
                }
            }

            // Update previous button states
            updatePreviousStates();

            // Display telemetry
            displayTelemetry();
        }
    }

    private void checkKonamiCode() {
        // Clear wrong input flag after a short time
        if (wrongInput && runtime.seconds() - wrongInputTime > 0.5) {
            wrongInput = false;
        }

        // Check if sequence has timed out
        if (currentStep > 0 && sequenceTimer.seconds() > SEQUENCE_TIMEOUT) {
            resetKonamiSequence();
            wrongInput = true;
            wrongInputTime = runtime.seconds();
            gamepad1.rumble(200);
            return;
        }

        // Check if enough time has passed since last input
        if (runtime.seconds() - lastInputTime < INPUT_COOLDOWN) {
            return;
        }

        String expectedInput = currentStep < KONAMI_BUTTONS.length ? KONAMI_BUTTONS[currentStep] : "";
        boolean correctInput = false;
        String pressedButton = "none";

        // Check for button presses
        if (gamepad1.dpad_up && !prevDpadUp) {
            pressedButton = "up";
            correctInput = expectedInput.equals("up");
        } else if (gamepad1.dpad_down && !prevDpadDown) {
            pressedButton = "down";
            correctInput = expectedInput.equals("down");
        } else if (gamepad1.dpad_left && !prevDpadLeft) {
            pressedButton = "left";
            correctInput = expectedInput.equals("left");
        } else if (gamepad1.dpad_right && !prevDpadRight) {
            pressedButton = "right";
            correctInput = expectedInput.equals("right");
        } else if (gamepad1.b && !prevB) {
            pressedButton = "b";
            correctInput = expectedInput.equals("b");
        } else if (gamepad1.a && !prevA) {
            pressedButton = "a";
            correctInput = expectedInput.equals("a");
        }

        if (!pressedButton.equals("none")) {
            lastPressedButton = pressedButton;
            lastInputTime = runtime.seconds();

            if (correctInput) {
                currentStep++;
                gamepad1.rumble(0, 50, 100);

                // Start sequence timer on first input
                if (currentStep == 1) {
                    sequenceTimer.reset();
                }

                // Check if code is complete
                if (currentStep >= KONAMI_CODE.length) {
                    codeActivated = true;
                    gamepad1.rumble(1000);
                }
            } else if (currentStep > 0) {
                // Wrong button pressed during sequence
                resetKonamiSequence();
                wrongInput = true;
                wrongInputTime = runtime.seconds();
                gamepad1.rumble(200);
            }
        }
    }

    private void resetKonamiSequence() {
        currentStep = 0;
        lastPressedButton = "none";
    }

    private void updatePreviousStates() {
        prevDpadUp = gamepad1.dpad_up;
        prevDpadDown = gamepad1.dpad_down;
        prevDpadLeft = gamepad1.dpad_left;
        prevDpadRight = gamepad1.dpad_right;
        prevA = gamepad1.a;
        prevB = gamepad1.b;
        prevRightBumper = gamepad1.right_bumper;
        prevLeftBumper = gamepad1.left_bumper;
        prevCircle = gamepad1.circle;
        prevSquare = gamepad1.square;
    }

    private void displayTelemetry() {
        telemetry.addData("═══ ROBOT STATUS ═══", "");
        telemetry.addData("Run Time", "%.1f sec", runtime.seconds());
        telemetry.addData("Speed", "%.1f %s", gowtham_speed, gowtham_speed >= 0.7 ? "(MAX)" : "");
        telemetry.addData("Drive Mode", !codeActivated ? "Normal" : "✨ KONAMI MODE ✨");

        telemetry.addData("", "");
        telemetry.addData("═══ KONAMI CODE ═══", "");

        // Display the Konami code sequence with progress
        String sequenceDisplay = "";
        String progressBar = "";

        for (int i = 0; i < KONAMI_CODE.length; i++) {
            if (i < currentStep) {
                sequenceDisplay += "[" + KONAMI_CODE[i] + "] ";
                progressBar += "█";
            } else if (i == currentStep) {
                if (wrongInput) {
                    sequenceDisplay += "❌ " + KONAMI_CODE[i] + " ";
                } else {
                    sequenceDisplay += spinnerFrames[spinnerIndex] + KONAMI_CODE[i] + spinnerFrames[spinnerIndex] + " ";
                }
                progressBar += "▒";
            } else {
                sequenceDisplay += KONAMI_CODE[i] + " ";
                progressBar += "░";
            }
        }

        telemetry.addData("Sequence", sequenceDisplay);
        telemetry.addData("Progress", progressBar + " " + currentStep + "/" + KONAMI_CODE.length);

        // Show timeout timer if sequence is active
        if (currentStep > 0 && !codeActivated) {
            double timeRemaining = SEQUENCE_TIMEOUT - sequenceTimer.seconds();
            String timeBar = "";
            int barLength = 20;
            int filledLength = (int)(barLength * (timeRemaining / SEQUENCE_TIMEOUT));

            for (int i = 0; i < barLength; i++) {
                timeBar += i < filledLength ? "▓" : "░";
            }

            telemetry.addData("Time Left", "%.1f sec %s", timeRemaining, timeBar);
        }

        if (wrongInput) {
            telemetry.addData("", "");
            telemetry.addData("STATUS", "❌ WRONG INPUT! Try again...");
        } else if (codeActivated) {
            telemetry.addData("", "");
            telemetry.addData("STATUS", "🎉 KONAMI CODE ACTIVATED! 🎉");
            telemetry.addData("", "✨ Secret Mode Unlocked! ✨");
        }

        telemetry.addData("", "");
        telemetry.addData("Last Input", lastPressedButton.toUpperCase());
        telemetry.addData("Controls", "□ = Reset Code | ○ = Toggle Drive");

        telemetry.update();
    }
}