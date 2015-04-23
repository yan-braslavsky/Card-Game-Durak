package com.yan.durak.layouting.threepoint;


import com.yan.durak.layouting.impl.CardsLayouterSlotImpl;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.List;

import glengine.yan.glengine.util.geometry.YANVector2;
import glengine.yan.glengine.util.math.YANMathUtils;

/**
 * Created by Yan-Home on 12/28/2014.
 */
public class ThreePointFanLayouter implements ThreePointLayouter {

    private YANVector2 mOriginPoint;
    private YANVector2 mLeftBasis;
    private YANVector2 mRightBasis;

    private YANVector2 mNormalizedOriginPoint;
    private YANVector2 mNormalizedLeftBasis;
    private YANVector2 mNormalizedRightBasis;
    private float mSourceFanAngle;
    private float mDestFanAngle;

    private RealMatrix mAffineMappingMatrix;
    private int mSmallestSortingLayer;
    private LayoutDirection mDirection;




    public ThreePointFanLayouter(int smallestSortingLayer) {
        mSmallestSortingLayer = smallestSortingLayer;
        mDirection = LayoutDirection.LTR;
    }

    @Override
    public void setThreePoints(YANVector2 originPoint, YANVector2 leftBasis, YANVector2 rightBasis) {
        mOriginPoint = originPoint;
        mLeftBasis = leftBasis;
        mRightBasis = rightBasis;

        //create normalized points that we are layouting to
        initNormalizedPoints();

        //create the affine matrix that will be used to map points
        //from normalized triangle to given triangle
        mAffineMappingMatrix = findAffineMatrixUsingSourceAndDestinationPoints();

        //find the angle between two sides of the triangle
        mDestFanAngle = calculateDestinationAngle(originPoint, leftBasis, rightBasis);

    }

    private float calculateDestinationAngle(YANVector2 originPoint, YANVector2 leftBasis, YANVector2 rightBasis) {

        //We have a triangle with 3 points (R,P,Q) and 3 angles between the sides (r,p,q)
        //now we are calculating all the sides in order to find an angle R

        double p = calculateDistanceBetween2Points(rightBasis, originPoint);
        double q = calculateDistanceBetween2Points(leftBasis, originPoint);
        double r = calculateDistanceBetween2Points(leftBasis, rightBasis);

        //now we can calculate the actual angle
        double angle = findAngleBetween2Sides(r, p, q);
        return (float) angle;
    }

    private double findAngleBetween2Sides(double oppositeToAngleSide, double rightToAngleSide, double leftToAngleSide) {
        //using the formula r^2 = p^2 + q^2 - 2pq*cos(R)
        //taken from http://www.mathsisfun.com/algebra/trig-solving-sas-triangles.html
        double p = rightToAngleSide;
        double q = leftToAngleSide;
        double r = oppositeToAngleSide;

        //find cos(R)
        double cosR = ((p * p) + (q * q) - (r * r)) / (2 * p * q);

        //find angle in degrees
        double angle = Math.toDegrees(Math.acos(cosR));
        return angle;
    }

    private double calculateDistanceBetween2Points(YANVector2 fromPoint, YANVector2 toPoint) {
        //taken from http://www.purplemath.com/modules/distform.htm
        float xDistance = fromPoint.getX() - toPoint.getX();
        float yDistance = fromPoint.getY() - toPoint.getY();
        return Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
    }

    //taken from :
    //http://stackoverflow.com/questions/21270892/generate-affinetransform-from-3-points
    private RealMatrix findAffineMatrixUsingSourceAndDestinationPoints() {

        //AffineMatrix * sourceTriangleMatrix = DestinationTriangleMatrix
        //AffinMatrix = DestinationTriangleMatrix * Inversion(sourceTriangleMatrix)

        //create matrix of the normalized points (source triangle)
        double[][] normalizedTriangleMatrixData = {

                //first row contains data for normalized triangle : nTopX,nLeftX,nRightX
                {mNormalizedOriginPoint.getX(), mNormalizedLeftBasis.getX(), mNormalizedRightBasis.getX()},

                //first row contains data for normalized triangle : nTopY,nLeftY,nRightY
                {mNormalizedOriginPoint.getY(), mNormalizedLeftBasis.getY(), mNormalizedRightBasis.getY()},

                //third row is an extention to 3D coordinates
                {1, 1, 1},
        };

        //create normalized triangle matrix
        RealMatrix normalizedTriangleMatrix = MatrixUtils.createRealMatrix(normalizedTriangleMatrixData);


        //create matrix data of the destination triangle
        double[][] destinationTriangleMatrixData = {

                //first row contains data for normalized triangle : nTopX,nLeftX,nRightX
                {mOriginPoint.getX(), mLeftBasis.getX(), mRightBasis.getX()},

                //first row contains data for normalized triangle : nTopY,nLeftY,nRightY
                {mOriginPoint.getY(), mLeftBasis.getY(), mRightBasis.getY()},
        };

        //create normalized triangle matrix
        RealMatrix destinationTriangleMatrix = MatrixUtils.createRealMatrix(destinationTriangleMatrixData);

        // Invert source matrix , using LU decomposition
        RealMatrix sourceTriangleMatrixInverse = new LUDecomposition(normalizedTriangleMatrix).getSolver().getInverse();

        // Now multiply destinationTriangleMatrix by sourceTriangleMatrixInverse
        RealMatrix affineMatrix = destinationTriangleMatrix.multiply(sourceTriangleMatrixInverse);

        return affineMatrix;
    }

    private void initNormalizedPoints() {

        //TODO : not efficient to allocate new vectors each time
        mNormalizedOriginPoint = new YANVector2(0, 0);
        mNormalizedLeftBasis = new YANVector2(1, 1);
        mNormalizedRightBasis = new YANVector2(-1, 1);

        //calculate the angle between 2 basis meridians
        float opposite = Math.abs(mNormalizedLeftBasis.getX() - mNormalizedRightBasis.getX()) / 2;
        float adjacent = Math.abs(mNormalizedOriginPoint.getY() - mNormalizedLeftBasis.getY());
        mSourceFanAngle = (float) Math.toDegrees(Math.atan(opposite / adjacent) * 2);
    }

    @Override
    public void layoutRowOfSlots(List<CardsLayouterSlotImpl> slots) {

        //we are are rotating left basis counter clockwise half the fan angle
        //to reach the centered highest point
        YANVector2 startingPositionVector = (mDirection == LayoutDirection.LTR) ? mNormalizedLeftBasis : mNormalizedRightBasis;

        int angleStepDivider = slots.size() - 1;
        int rotationStepDivider = slots.size();

        //correction for edge cases
        if (slots.size() == 1) {
            angleStepDivider = 1;
            rotationStepDivider = 2;
        }

        float angleStep = mSourceFanAngle / angleStepDivider;
        float rotationStep = mDestFanAngle / rotationStepDivider;

        if (mDirection == LayoutDirection.RTL) {
            rotationStep *= -1;
            angleStep *= -1;
        }


        //rotate slots
        for (int i = 0; i < slots.size(); i++) {
            CardsLayouterSlotImpl slot = slots.get(i);

            //set slot to initial position and rotation
            slot.setPosition(startingPositionVector.getX(), startingPositionVector.getY());
            slot.setRotation(rotationStep * (i + 1));
            slot.setSortingLayer(mSmallestSortingLayer + i);

            YANMathUtils.rotatePointAroundOrigin(slot.getPosition(), mNormalizedOriginPoint, angleStep * i);

            //Map back ...
            //create vector for slot point
            double[][] pointData = {
                    {slot.getPosition().getX()},
                    {slot.getPosition().getY()},
                    {1}
            };
            RealMatrix slotNormalizedMatrix = MatrixUtils.createRealMatrix(pointData);
            RealMatrix slotMappedBackMatrix = mAffineMappingMatrix.multiply(slotNormalizedMatrix);

            double[] column = slotMappedBackMatrix.getColumn(0);
            slot.setPosition((float) column[0], (float) column[1]);
        }
    }

    @Override
    public void setDirection(LayoutDirection direction) {
        mDirection = direction;
    }
}
