package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
// Import Location enums. (Detector.Location)
import org.firstinspires.ftc.teamcode.autonomous.vision.DuckDetector;
import org.firstinspires.ftc.teamcode.autonomous.vision.TseDetector;

import org.openftc.easyopencv.OpenCvCamera;

import java.util.Arrays;

@Autonomous(name="Blue Alliance Left", group="FTC22")
public class BlueAllianceLeft extends LinearOpMode {
    OpenCvCamera webcam;
    DcMotorEx backLeft;
    DcMotorEx backRight;
    DcMotorEx frontLeft;
    DcMotorEx frontRight;
    DcMotorEx armClaw;
    DcMotorEx collector;
    DcMotorEx duckSpinner1;
    DcMotorEx duckSpinner2;
    Robot robot;

    private void initMotors() {
        backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
        frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        backRight = hardwareMap.get(DcMotorEx.class, "backRight");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        armClaw = hardwareMap.get(DcMotorEx.class, "armClaw");
        collector = hardwareMap.get(DcMotorEx.class, "collector");
        duckSpinner1 = hardwareMap.get(DcMotorEx.class, "duckSpinner1");
        duckSpinner2 = hardwareMap.get(DcMotorEx.class, "duckSpinner2");
    }

    @Override
    public void runOpMode(){
        initMotors();
        robot = new Robot(Arrays.asList(backLeft, frontLeft, backRight, frontRight, armClaw, collector, duckSpinner1, duckSpinner2), this);

        DuckDetector.Location duckPos = robot.getDuckPos();
        waitForStart();
        switch (duckPos) {
            case LEFT:
                robot.drive(Robot.Direction.LEFT, 0.5, 5000);
                break;
            case RIGHT:
                robot.strafe(Robot.Direction.LEFT, 0.5, 5000);
                break;
            case CENTER:
                robot.turn(Robot.Direction.LEFT, 1, 90);
                break;
        }
    }
}
