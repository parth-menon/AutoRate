package com.steelsty.autorate;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;

public class Splash extends Activity
{
	OurView v;
	Bitmap ball, b1, b2, b3, b4, b5, b6, logo;
	float x, y;
	int i = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		v = new OurView(this);
		b1 = BitmapFactory.decodeResource(getResources(), R.drawable.b1);
		b2 = BitmapFactory.decodeResource(getResources(), R.drawable.b2);
		b3 = BitmapFactory.decodeResource(getResources(), R.drawable.b3);
		b4 = BitmapFactory.decodeResource(getResources(), R.drawable.b4);
		b5 = BitmapFactory.decodeResource(getResources(), R.drawable.b5);
		b6 = BitmapFactory.decodeResource(getResources(), R.drawable.b6);
		logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
		ball = b1;
		x = y = 0;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(v);
	}

	@Override
	protected void onDestroy()
	{
		v.destroy();
		super.onDestroy();
	}

	@Override
	protected void onResume()
	{
		v.resume();
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		v.pause();
		super.onPause();
	}

	public class OurView extends SurfaceView implements Runnable
	{
		Thread t = null;
		SurfaceHolder holder;
		boolean isitok = false;
		boolean stopit = true;
		MediaPlayer media, med;

		public OurView(Context context)
		{
			super(context);
			holder = getHolder();
		}

		@Override
		public void run()
		{
			while (isitok == true)
			{
				if (!holder.getSurface().isValid())
				{
					continue;
				}
				Canvas c = holder.lockCanvas();
				c.drawColor(Color.parseColor("#ff8800"));
				y = c.getHeight() / 2;
				x += 6;
				i = (i + 1) % 4;
				if (x > (c.getWidth() / 2 - ball.getWidth() / 2))
					ball = b5;
				else if (i == 0)
					ball = b1;
				else if (i == 1)
					ball = b2;
				else if (i == 2)
					ball = b3;
				else if (i == 3)
					ball = b4;
				if (x > c.getWidth() / 2)
				{
					isitok = false;
					stopit=false;
					ball = b6;
					med = MediaPlayer.create(Splash.this, R.raw.ring);
					med.start();
					c.drawBitmap(
							logo,
							x - (logo.getWidth() / 2),
							y - (ball.getHeight() * 2) - (logo.getHeight() / 2),
							null);
				}
				c.drawBitmap(ball, x - (ball.getWidth() / 2),
						y - (ball.getHeight() / 2), null);
				holder.unlockCanvasAndPost(c);
				if (stopit == false)
				{
					try
					{
						Thread.sleep(2000);
					} catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					finish();
					onDestroy();
				}
			}
		}

		public void pause()
		{
			isitok = false;
			try
			{
				t.join();

			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			t = null;
		}

		public void destroy()
		{
			isitok=false;
			if(media.isPlaying())
				media.stop();
			finish();
			
		}

		public void resume()
		{
			isitok = true;
			t = new Thread(this);
			x = 0;
			y = 0;
			media = MediaPlayer.create(Splash.this, R.raw.splash);
			media.start();
			t.start();
		}

	}

}
