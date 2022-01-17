package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.vision.DuckDetector;
import org.firstinspires.ftc.teamcode.vision.TseDetector;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.List;

import com.acmerobotics.dashboard.config.Config;

@Config
public class Robot {
    private long clawTime = 0;

    HardwareMap hardwareMap;
    DcMotor frontLeft;
    DcMotor backLeft;
    DcMotor frontRight;
    DcMotor backRight;
    DcMotor collector;
    DcMotor armClaw;
    DcMotor duckSpinner1;
    DcMotor duckSpinner2;
    BNO055IMU imu;
    TouchSensor touchSensorSideLeft;
    TouchSensor touchSensorSideRight;
    TouchSensor touchSensorFrontRight;
    
    private enum Axis {
        X,
        Y,
        Z
    }

    public enum Direction {
        LEFT,
        RIGHT,
        FORWARD,
        BACK,
        UP,
        DOWN,
        IN,
        OUT
    }

    public enum Position {
        LOW,
        MID,
        HIGH,
        DOWN
    }

    //TODO: Place the three touch sensors accordingly
    public String inContact() {
        touchSensorSideLeft = hardwareMap.get(TouchSensor.class, "rightSideTouch");
        touchSensorSideRight = hardwareMap.get(TouchSensor.class, "rightSideTouch");
        touchSensorFrontRight = hardwareMap.get(TouchSensor.class, "frontRightTouch");

        if (touchSensorSideLeft.isPressed()) {
            return "Left";
        } else if (touchSensorSideRight.isPressed()) {
            return "Right";
        } else if (touchSensorFrontRight.isPressed()){
            return "Center";
        } else {
            return null;
        }
    }

    private void initIMU() {
        BNO055IMU.Parameters params = new BNO055IMU.Parameters();
        params.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        params.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(params);
    }

    private float getIMUAngle(Axis axis) {
        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        switch(axis) {
            case X:
                return angles.thirdAngle;
            case Y:
                return angles.secondAngle;
            case Z:
                return angles.firstAngle;
                
        }
        return 0;
    }

    public DuckDetector.Location getDuckPos() {
        DuckDetector detector;
        detector = new DuckDetector();
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        OpenCvWebcam webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        webcam.openCameraDevice();
        webcam.setPipeline(detector);
        webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
        DuckDetector.Location location = detector.getLocation();
        webcam.stopStreaming();
        webcam.closeCameraDevice();
        return location;
    }

    public TseDetector.Location getTsePos() {
        TseDetector detector;
        detector = new TseDetector();
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        OpenCvWebcam webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        webcam.openCameraDevice();
        webcam.setPipeline(detector);
        webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
        TseDetector.Location location = detector.getLocation();
        webcam.stopStreaming();
        webcam.closeCameraDevice();
        return location;
    }
    

    public void STOP() {
        frontLeft.setPower(0);
        backLeft.setPower(0);
        frontRight.setPower(0);
        backRight.setPower(0);
    }

    public void strafe(Direction dir, double power, long time) {
        long timeMillis = 0;
        switch(dir) {
            case LEFT:
                while(System.currentTimeMillis()+time > timeMillis) {
                    timeMillis = System.currentTimeMillis();
                    frontLeft.setPower(power);
                    frontRight.setPower(-power);
                    backLeft.setPower(-power);
                    backRight.setPower(power);
                }
                this.STOP();
                break;
            case RIGHT:
                while(System.currentTimeMillis()+time > timeMillis) {
                    timeMillis = System.currentTimeMillis();
                    frontLeft.setPower(-power);
                    frontRight.setPower(power);
                    backLeft.setPower(power);
                    backRight.setPower(-power);
                }
                this.STOP();
                break;
        }
    }
    
    public void drive(Direction dir, double power, long time) {
        long timeMillis = 0;
        // TODO: adjust gain.
        double gain = 0.5;
        float targetAngle = this.getIMUAngle(Axis.Z);
        switch (dir){
            case FORWARD:
                while(System.currentTimeMillis()+time > timeMillis) {
                    float angle = this.getIMUAngle(Axis.Z);
                    double correction = (angle-targetAngle)*gain;
                    timeMillis = System.currentTimeMillis();
                    frontLeft.setPower(-power);
                    frontRight.setPower(power-correction);
                    backLeft.setPower(-power);
                    backRight.setPower(power-correction);
                    }
                this.STOP();
                break;
            case BACK:
                while(System.currentTimeMillis()+time > timeMillis) {
                    float angle = this.getIMUAngle(Axis.Z);
                    double correction = (angle-targetAngle)*gain;
                    timeMillis = System.currentTimeMillis();
                    frontLeft.setPower(power);
                    frontRight.setPower(-(power-correction));
                    backLeft.setPower(power);
                    backRight.setPower(-(power-correction));
                    }
                this.STOP();
                break;
        } 
    }
        
    public void turn(Direction dir, double power, float degrees) {
        float targetAngle;
        switch (dir){
            case LEFT:
                targetAngle = this.getIMUAngle(Axis.Z)+degrees;
                while(this.getIMUAngle(Axis.Z) != targetAngle) {
                    frontLeft.setPower(power);
                    frontRight.setPower(power);
                    backLeft.setPower(power);
                    backRight.setPower(power);
                }
                this.STOP();
                break;
            case RIGHT:
                targetAngle = this.getIMUAngle(Axis.Z)-degrees;
                while(this.getIMUAngle(Axis.Z) != targetAngle) {
                    frontLeft.setPower(-power);
                    frontRight.setPower(-power);
                    backLeft.setPower(-power);
                    backRight.setPower(-power);
                }
                this.STOP();
                break;
            } 
    }

    public void moveClaw(Position pos) {
        long timeMillis = 0;
        //TODO: Calibrate the time needed for each of the 3 levels
        long lowTime = 0;
        long midTime = 0;
        long highTime = 0;
        long newTime;
        switch(pos) {
            case LOW:
                newTime = lowTime-clawTime;
                if(newTime > 0) {
                    while(System.currentTimeMillis()+newTime > timeMillis) {
                        timeMillis = System.currentTimeMillis();
                        armClaw.setPower(1);
                    }
                } else {
                    newTime = clawTime-lowTime;
                    while(System.currentTimeMillis()+newTime > timeMillis) {
                        timeMillis = System.currentTimeMillis();
                        armClaw.setPower(-1);
                    }
                }
                this.clawTime = newTime;
                armClaw.setPower(0);
                break;
            case MID:
                newTime = midTime-clawTime;
                if(newTime > 0) {
                    while(System.currentTimeMillis()+newTime > timeMillis) {
                        timeMillis = System.currentTimeMillis();
                        armClaw.setPower(1);
                    }
                } else {
                    newTime = clawTime-midTime;
                    while(System.currentTimeMillis()+newTime > timeMillis) {
                        timeMillis = System.currentTimeMillis();
                        armClaw.setPower(-1);
                    }
                }
                this.clawTime = newTime;
                armClaw.setPower(0);
                break;
            case HIGH:
                newTime = highTime-clawTime;
                if(newTime > 0) {
                    while(System.currentTimeMillis()+newTime > timeMillis) {
                        timeMillis = System.currentTimeMillis();
                        armClaw.setPower(1);
                    }
                } else {
                    newTime = clawTime-highTime;
                    while(System.currentTimeMillis()+newTime > timeMillis) {
                        timeMillis = System.currentTimeMillis();
                        armClaw.setPower(-1);
                    }
                }
                this.clawTime = newTime;
                armClaw.setPower(0);
                break;
            case DOWN:
                while(System.currentTimeMillis()+clawTime > timeMillis) {
                    timeMillis = System.currentTimeMillis();
                    armClaw.setPower(-1);
                }
                this.clawTime = 0;
                break;
        }
    }

    public void intake(Direction dir) {
        long timeToMove = 1000;
        long timeMillis = 0;
        switch(dir){
            case IN:
                while(System.currentTimeMillis()+timeToMove > timeMillis) {
                    timeMillis = System.currentTimeMillis();
                    collector.setPower(0.6);
                }
                collector.setPower(0);
                break;
            case OUT:
                while(System.currentTimeMillis()+timeToMove > timeMillis) {
                    timeMillis = System.currentTimeMillis();
                    collector.setPower(-0.6);
                    }
                collector.setPower(0);
                break;
            }
    }

    public void duckSpin() {
        // TODO: calibrate time to spin.
        long timeToSpin = 1000;
        long timeMillis = 0;
        while(System.currentTimeMillis()+timeToSpin > timeMillis) {
            timeMillis = System.currentTimeMillis();
            duckSpinner1.setPower(1);
            duckSpinner2.setPower(1);
        }
        duckSpinner1.setPower(0);
        duckSpinner2.setPower(0);
    }

    // Class constructor. -
    // Important init code. Modify only if needed.
    public Robot(List<DcMotor> motors, HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
        backLeft = motors.get(0);
        frontLeft = motors.get(1);
        backRight = motors.get(2);
        frontRight = motors.get(4);
        armClaw = motors.get(5);
        collector = motors.get(6);
        duckSpinner1 = motors.get(7);
        duckSpinner2 = motors.get(8);
        initIMU();
    }

}