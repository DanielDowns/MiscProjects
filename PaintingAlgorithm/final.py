#http://rosettacode.org/wiki/Bitmap/Midpoint_circle_algorithm#Python
#http://www.cs.virginia.edu/~dbrogan/CS551.851.animation.sp.2000/Papers/p407-litwinowicz.pdf

from PIL import Image, ImageFilter
#import os
#from os import path
import time
import pdb
import math
import copy
import sys
from random import randint, uniform

#set to True to use Random Strokes Algorithm, set to False for Noisy Clipped Strokes Algorithm
randomDraw = True

#stroke radius for NCSA
radius = 2 
#space between strokes for NCSA
gap = 3

imString = ###absolute file name should go here
im = Image.open(imString, 'r')	
edges = im.filter(ImageFilter.FIND_EDGES)

stroke = Image.open("C:\Users\User\Pictures\stroke.png")

canvas = Image.new('RGB', im.size,  (255, 255, 255))
width = im.size[0]
height = im.size[1]

def isNull(pixelArray):
	if(pixelArray[0] == 0 and pixelArray[1] == 0 and pixelArray[2] == 0 and pixelArray[3] == 0):
		return True
	return False
	
def isBlack(pixelArray):
	if(pixelArray[0] == 0 and pixelArray[1] == 0 and pixelArray[2] == 0):
		return True
	return False

def isWhite(pixelArray):
	if(pixelArray[0] == 255 and pixelArray[1] == 255 and pixelArray[2] == 255):
		return True
	return False	
	
	
class dline:
	color = (1,0,0) #red
	
	point1 = (50,50)
	point2 = (100,100)
	
	def setPoints(self, x1, y1, x2, y2):
		self.point1 = (x1, y1)
		self.point2 = (x2, y2)
	
	def circle(self, x0, y0, rad, col):
		f = 1 - rad
		ddf_x = 1
		ddf_y = -2 * rad
		x = 0
		y = rad
		
		if(x0 - rad <= 0 or x0 + rad >= width or y0 - rad <= 0 or y0 + rad >= height):
			return
		
		canvas.putpixel((x0, y0 + rad), col)
		canvas.putpixel((x0, y0 - rad), col)
		canvas.putpixel((x0 + rad, y0), col)
		canvas.putpixel((x0 - rad, y0), col)
		
		while (x < y):
			if f >= 0: 
				y -= 1
				ddf_y += 2
				f += ddf_y
			x += 1
			ddf_x += 2
			f += ddf_x 
	
			canvas.putpixel((x0 + x, y0 + y), col)
			canvas.putpixel((x0 - x, y0 + y), col)
			canvas.putpixel((x0 + x, y0 - y), col)
			canvas.putpixel((x0 - x, y0 - y), col)
			canvas.putpixel((x0 + y, y0 + x), col)
			canvas.putpixel((x0 - y, y0 + x), col)
			canvas.putpixel((x0 + y, y0 - x), col)
			canvas.putpixel((x0 - y, y0 - x), col)
			
	def render(self, image, detail="low"):
		steep = False
		slope_a = (self.point2[1] - self.point1[1])
		slope_b = (self.point2[0] - self.point1[0])
		if(slope_b == 0):
			slope_b = .0000001
		slope = float(slope_a)/float(slope_b)
		if(abs(slope) > 1):
			steep = True
			
		start = 0
		end = 0
		rise = 0
		
		if(steep == True):
			if(self.point1[1] > self.point2[1]):
				temp = self.point2
				self.point2 = self.point1
			
			start = self.point1[1]
			end = self.point2[1]
			rise = self.point1[0]
			slope = 1/slope
		else:
			if(self.point1[0] > self.point2[0]):
				temp = self.point2
				self.point2 = self.point1
				self.point1 = temp
			start = self.point1[0]
			end = self.point2[0]
			rise = self.point1[1]
			
		if(slope == 90):
			slope = 89.99999999999
			
		while (start < end):
			rise += slope
			
			mark = radius
			if(steep == True):
				if(start > 0 and start < height - 1 and int(round(rise)) > 0 and int(round(rise)) < width - 1):
					image.putpixel((int(round(rise)), int(start)), self.color)
					while(mark > 0):		
						self.circle(int(round(rise)), int(start), mark, self.color)
						if(detail == "low"):
							mark -= 2
						else:
							mark -= 1
			else:
				if(start > 0 and start < width - 1 and int(round(rise)) > 0 and int(round(rise)) < height - 1):
					image.putpixel((int(start), int(round(rise))), self.color)
					while(mark > 0):
						self.circle(int(start), int(round(rise)), mark, self.color)
						if(detail == "low"):
							mark -= 2
						else:
							mark -= 1
			start += 1


def paintStroke(xPos, yPos, (R, G, B), limit, lengthLimit=0, angle=-1):
	smallStroke = stroke	
	if(lengthLimit == 0):
		smallStroke = smallStroke.resize((width/limit, height/limit))
	else:
		smallStroke = smallStroke.resize((int(lengthLimit), height/(limit/5)))
	if(angle == -1):	
		smallStroke = smallStroke.rotate(randint(0, 359))
	else:
		smallStroke = smallStroke.rotate(angle)
	
	i = 0
	j = 0
	while(i < smallStroke.size[0]):
		while(j < smallStroke.size[1]):
			base = smallStroke.getpixel((i,j))
			if(isWhite(base) == True or isNull(base) == True):
				j += 1
				continue

			finalX = i + xPos
			finalY = j + yPos
			if(finalX < width and finalY < height):
				if(finalX > 0 and finalY > 0):
					canvas.putpixel((finalX,finalY), ((R, G, B)))
			j += 1
			
		j = 0
		i += 1

if(randomDraw != True):
	print("Using Clipping algorithm...")
	start = time.time()
	addRandom = True

	secondMark = 0
	i = 0
	j = 0
	while(i < width - 1):
		while(j < height - 1):
	
			w = i
			h = j
			col = im.getpixel((w, h))
					
			blX = w
			blY = h
			trX = w
			trY = h
			bxNoise = 0
			byNoise = 0
			txNoise = 0
			tyNoise = 0
			maxLength = 100000
			
			colr = col
			if(addRandom is True):
				bxNoise = randint(0, 1)
				byNoise = randint(0, 1)
				txNoise = randint(0, 1)
				tyNoise = randint(0, 1)
				maxLength = randint(30, 75)
				R = col[0] + randint(-10, 10)
				if(R < 0):
					R = 0
				if(R > 255):
					R = 255
					
				G = col[1] + randint(-10, 10)
				if(G < 0):
					G = 0
				if(G > 255):
					G = 255	
			
				B = col[2] + randint(-10, 10)
				if(B < 0):
					B = 0
				if(B > 255):
					B = 255
				colr=(R, G, B)
			
			line = dline()
			line.color = colr

				
			#check if point is on line
			edgeColor = edges.getpixel((w,h))				
			
			swap = randint(0, 1)
			swap = 0
			size = 0
			growTR = True
			growBL = True
			
			while(growTR is True or growBL is True):
				if(growBL is True):
					blX -= (1 + bxNoise)
					if(swap == 0):
						blY += (1 + byNoise)
					else:
						blY -= (1 + byNoise)
						
					if(blX <= 0 or blY <= 0 or blX >= width or blY >= height):
						growBL = False
					else:
						edgeColor = edges.getpixel((blX, blY))
						
						if(isBlack(edgeColor) != True):
							growBL = False
					
				
				if(growTR is True):
					trX += (1 + txNoise)
					if(swap == 0):
						trY -= (1 + tyNoise)
					else:
						trY += (1 + tyNoise)
						
					if(trX >= width or trY >= height or trX <= 0 or trY <= 0):
						growTR = False
					else:
						edgeColor = edges.getpixel((trX, trY))
						
						if(isBlack(edgeColor) != True):
							growTR = False
					
				if(math.hypot(blX - trX, blY - trY)	>= maxLength):
					growTR = False
					growBL = False
					
			size = math.hypot(blX - trX, blY - trY)
			line.setPoints(blX, blY, trX, trY)
			line.render(canvas, "high")
			
			j += gap
			j += randint(-1,1)
			
		i += gap
		i += randint(-1,1)
		
		left = width - i
		if(left % 50 == 0):
			print(i)
		j = 0	
			
	end = time.time()
	minutes = (end-start)/60
	seconds = (end-start) % 60
	print("total elapsed time: "+str(minutes)+" minutes, "+str(seconds)+" seconds")
		
	canvas.show()
			
else:
	print("Using randomizing algorithm")
	
	start = time.time()
	#small strokes
	limiter = 25
#	strokeCount = 140000
	strokeCount = 30000


	i = 0
	j = 0
	while(strokeCount > 0):
		
		w = randint(0, width - 1)
		h = randint(0, height - 1)
		
		color = im.getpixel((w, h))
		
		paintStroke(w, h, color, limiter)
		
		strokeCount -= 1
		if(strokeCount % 1000 == 0):
			print(str(strokeCount) + " small strokes left to paint!")

		
	end = time.time()
	minutes = (end-start)/60
	seconds = (end-start) % 60
	print("total elapsed time: "+str(minutes)+" minutes, "+str(seconds)+" seconds")
	canvas.show()
