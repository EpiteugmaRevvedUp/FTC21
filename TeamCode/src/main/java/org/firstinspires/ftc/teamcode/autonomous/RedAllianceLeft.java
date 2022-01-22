package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
// Import Location enums. (Detector.Location)
import org.firstinspires.ftc.teamcode.vision.DuckDetector;
import org.firstinspires.ftc.teamcode.vision.TseDetector;

import org.openftc.easyopencv.OpenCvCamera;

import java.util.Arrays;

@Autonomous(name="Red Alliance Left", group="FTC22")
public class RedAllianceLeft extends LinearOpMode {
    OpenCvCamera webcam;
    DcMotor backLeft;
    DcMotor backRight;
    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor armClaw;
    DcMotor collector;
    DcMotor duckSpinner1;
    DcMotor duckSpinner2;
    Robot robot;

    private void initMotors() {
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        armClaw = hardwareMap.get(DcMotor.class, "armClaw");
        collector = hardwareMap.get(DcMotor.class, "collector");
        duckSpinner1 = hardwareMap.get(DcMotor.class, "duckSpinner1");
        duckSpinner2 = hardwareMap.get(DcMotor.class, "duckSpinner2");
    }

    @Override
    public void runOpMode(){
        initMotors();
        robot = new Robot(Arrays.asList(backLeft, frontLeft, backRight, frontRight, armClaw, collector, duckSpinner1, duckSpinner2), this);

        DuckDetector.Location duckPos = robot.getDuckPos();
        waitForStart();
        switch (duckPos) {
            case LEFT:
                robot.moveClaw(Robot.Position.LOW);
                break;
            case RIGHT:
                robot.moveClaw(Robot.Position.HIGH);
                break;
            case CENTER:
                robot.moveClaw(Robot.Position.MID);
                break;
        }
    }
}
