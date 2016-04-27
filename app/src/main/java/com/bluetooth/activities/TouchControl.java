/**
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.bluetooth.activities;

import com.bluetooth.BluetoothActivity;
import com.bluetooth.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

public class TouchControl extends BluetoothActivity implements OnTouchListener, SurfaceHolder.Callback {
    private TextView tvTouchX, tvTouchY;
    private Button bToggle;
    private SurfaceView svTouchArea;
    private SurfaceHolder svTouchAreaHolder;
    private Canvas canvas;
    private Bitmap joystick;
    private int touchX, touchY;
    private boolean running;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.touch_control);

        tvTouchX = (TextView) findViewById(R.id.tvTouchX);
        tvTouchX.setText("X: 0");
        tvTouchY = (TextView) findViewById(R.id.tvTouchY);
        tvTouchY.setText("Y: 0");

        svTouchArea = (SurfaceView) findViewById(R.id.svTouchArea);
        // Needed to make the SurfaceView background transparent
        svTouchArea.setZOrderOnTop(true);
        svTouchAreaHolder = svTouchArea.getHolder();
        svTouchAreaHolder.addCallback(this);
        svTouchAreaHolder.setFormat(PixelFormat.TRANSPARENT);
        svTouchArea.setOnTouchListener(this);

        joystick = BitmapFactory.decodeResource(getResources(), R.drawable.joystick);

        bToggle = (Button) findViewById(R.id.bToggle);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                // Stop robot
                touchX = 0;
                touchY = 0;
                drawJoystick(v.getWidth() / 2, v.getHeight() / 2);
                write("P");
                break;
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                int MAX_THROTTLE = 255;
                int MIN_THROTTLE = 150;

                touchX = 90 + (int) (90 * (2 * (float) event.getX() / v.getWidth() - 1));
                touchY = (int) (MAX_THROTTLE * (1 - 2 * (float) event.getY() / v.getHeight()));

                drawJoystick(event.getX(), event.getY());

                int throttle = Math.abs(touchY);
                throttle = throttle < MIN_THROTTLE ? MIN_THROTTLE : throttle;
                throttle = throttle > MAX_THROTTLE ? MAX_THROTTLE : throttle;

                if (running) {
                    if (touchY > 0) {
                        write("F " + throttle);
                    } else {
                        write("B " + throttle);
                    }

                    write("S " + touchX);
                }
                break;
        }

        tvTouchX.setText("X: " + touchX);
        tvTouchY.setText("Y: " + touchY);

        return true;
    }

    public void buttonClick(View v) {
        running = !running;
        if (running) {
            bToggle.setText(R.string.stop);
        } else {
            bToggle.setText(R.string.start);
        }
    }

    private void drawJoystick(float x, float y) {
        canvas = svTouchAreaHolder.lockCanvas();
        canvas.drawColor(0, Mode.CLEAR);
        canvas.drawBitmap(joystick, x - joystick.getWidth() / 2, y - joystick.getHeight() / 2, null);
        svTouchAreaHolder.unlockCanvasAndPost(canvas);
    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // Not used
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // Draw the joy stick on the surface as soon as ready
        drawJoystick(svTouchArea.getWidth() / 2, svTouchArea.getHeight() / 2);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Not used
    }
}
