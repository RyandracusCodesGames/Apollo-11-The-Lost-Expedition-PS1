
package com.ryancodesgames.apollo.mathlib;

public class Vec3D 
{
    public double x, y, z, w;
    
    public Vec3D(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 1;
    }
    
    public Vec3D addVector(Vec3D in, Vec3D in2)
    {
        Vec3D out = new Vec3D(in.x + in2.x, in.y + in2.y, in.z + in2.z);
        
        return out;
    }
    
    public Vec3D subtractVector(Vec3D in, Vec3D in2)
    {
        Vec3D out = new Vec3D(in.x - in2.x, in.y - in2.y, in.z - in2.z);
        
        return out;
    }
    
    public Vec3D multiplyVector(Vec3D in, double f)
    {
        Vec3D out = new Vec3D(in.x * f, in.y * f, in.z * f);
        
        return out;
    }
    
    public Vec3D divideVector(Vec3D in, double f)
    {
        Vec3D out = new Vec3D(in.x / f, in.y / f, in.z / f);
        
        return out;
    }
    
    public double dotProduct(Vec3D in, Vec3D in2)
    {
        return (in.x * in2.x + in.y * in2.y + in.z * in2.z);
    }
    
    public double vectorLength(Vec3D in)
    {
        return Math.sqrt(dotProduct(in, in));
    }
    
    public double Q_rsqrt(float x) 
    {
        float xhalf = 0.5f * x;
        int i = Float.floatToIntBits( x );
        i = 0x5f3759df - (i >> 1);
        x = Float.intBitsToFloat( i );
        x = x * (1.5f - (xhalf * x * x));
        return x;
    }
    
    public Vec3D normalize(Vec3D in)
    {
        double l = vectorLength(in);
        
        return new Vec3D(in.x / l, in.y / l, in.z / l);
    }
    
    public Vec3D crossProduct(Vec3D in, Vec3D in2)
    {
        Vec3D out = new Vec3D(0,0,0);
        
        out.x = in.y * in2.z - in.z * in2.y;
        out.y = in.z * in2.x - in.x * in2.z;
        out.z = in.x * in2.y - in.y * in2.x;
        
        return out;
    }
    
    public Vec3D barycenter(Vec3D p, Vec3D v1, Vec3D v2, Vec3D v3)
    {
        Vec3D ret;
        
        double d = (v2.y-v3.y) * (v1.x-v3.x) + (v3.x-v2.x) * (v1.y - v3.y);
        double u = ((v2.y-v3.y) * (p.x-v3.x) + (v3.x-v2.x) * (p.y-v3.y)) / d;
        double v = ((v3.y-v1.y) * (p.x-v3.x) + (v1.x-v3.x) * (p.y-v3.y)) / d;
        double w = 1.0f - u - v;
        
        ret = new Vec3D(u, v, w);
        
        return ret;
    }
    
    public Vec3D distance(Vec3D pos, Vec3D target)
    {
        Vec3D distance = subtractVector(target, pos);
        distance = normalize(distance);
        
        return distance;
    }
    
    public double dist(Vec3D pos, Vec3D target)
    {
        Vec3D distance = subtractVector(target, pos);
        
        double dist = vectorLength(distance);
        
        return dist;
    }
    
    public void inverseVector(Vec3D in)
    {
        in.x *= -1;
        in.y *= -1;
        in.z *= -1;
    }

    public boolean compareVector(Vec3D in, Vec3D in2)
    {
        if(in.x != in2.x)
        {
            return false;
        }
        else if(in.y != in2.y)
        {
            return false;
        }
        else return(in.z == in2.z);
    }
    
    public boolean vectorsAreCloseEnough(Vec3D in, Vec3D in2, double err)
    {
        if(Math.abs(in.x - in2.x) < err)
        {
            return true;
        }
        else if(Math.abs(in.y - in2.y) < err)
        {
            return true;
        }
        else return(Math.abs(in.z - in2.z) < err);
    }
    
    public void scale(Vec3D in, double scale, boolean multiply)
    {
        if(multiply)
        {
            in.x *= scale;
            in.y *= scale;
            in.z *= scale;
        }
        else
        {
            in.x /= scale;
            in.y /= scale;
            in.z /= scale;
        }
    }
    
     public Vec3D vectorIntersectPlane(Vec3D plane_p, Vec3D plane_n, Vec3D lineStart, Vec3D lineEnd, ExtraData tt)
    {
        plane_n = normalize(plane_n);
	double plane_d = -dotProduct(plane_n, plane_p);
	double ad = dotProduct(lineStart, plane_n);
	double bd = dotProduct(lineEnd, plane_n);
	double t = (-plane_d-ad)/(bd-ad);
        tt.t = t;
	Vec3D lineStartToEnd = subtractVector(lineEnd, lineStart);
	Vec3D lineToIntersect = multiplyVector(lineStartToEnd, t);
	return addVector(lineStart, lineToIntersect);
    }
    
    public int triangleClipAgainstPlane(Vec3D plane_p, Vec3D plane_n, Triangle in, Triangle[] out)
    {
        plane_n = normalize(plane_n);
        
        Vec3D[] inside_points = {new Vec3D(0,0,0), new Vec3D(0,0,0), new Vec3D(0,0,0)};
        int nInsidePointCount = 0;
        
        Vec3D[] outside_points = {new Vec3D(0,0,0), new Vec3D(0,0,0), new Vec3D(0,0,0)};
        int nOutsidePointCount = 0;
        
        Vec2D[] inside_tex = {new Vec2D(0,0), new Vec2D(0,0), new Vec2D(0,0)};
        int nInsideTexCount = 0;
        
        Vec2D[] outside_tex = {new Vec2D(0,0), new Vec2D(0,0), new Vec2D(0,0)};
        int nOutsideTexCount = 0;
        
        double d0 = dist(in.vec3d, plane_n, plane_p);
        double d1 = dist(in.vec3d2, plane_n, plane_p);
        double d2 = dist(in.vec3d3, plane_n, plane_p);
        
        if (d0 >= 0) { inside_points[nInsidePointCount++] = in.vec3d; inside_tex[nInsideTexCount++] = in.vec2d;}
	else { outside_points[nOutsidePointCount++] = in.vec3d; outside_tex[nOutsideTexCount++] = in.vec2d;}
	if (d1 >= 0) { inside_points[nInsidePointCount++] = in.vec3d2; inside_tex[nInsideTexCount++] = in.vec2d2;}
	else { outside_points[nOutsidePointCount++] = in.vec3d2; outside_tex[nOutsideTexCount++] = in.vec2d2; }
	if (d2 >= 0) { inside_points[nInsidePointCount++] = in.vec3d3; inside_tex[nInsideTexCount++] = in.vec2d3;}
	else { outside_points[nOutsidePointCount++] = in.vec3d3; outside_tex[nOutsideTexCount++] = in.vec2d3;}

        
        if (nInsidePointCount == 0)
	{
            // All points lie on the outside of plane, so clip whole triangle
            // It ceases to exist

            return 0; // No returned triangles are valid
	}

	if (nInsidePointCount == 3)
	{
	// All points lie on the inside of plane, so do nothing
	// and allow the triangle to simply pass through
            out[0] = in;

            return 1; // Just the one returned original triangle is valid
	}
        
        if(nInsidePointCount == 1 && nOutsidePointCount == 2)
        {
            out[0].col = in.col;
            out[0].tex = in.tex;
            out[0].dp = in.dp;
            out[0].vec3d = inside_points[0];
            out[0].vec2d = inside_tex[0];
            
            ExtraData t = new ExtraData(0);

            out[0].vec3d2 = vectorIntersectPlane(plane_p, plane_n, inside_points[0], outside_points[0], t);
            out[0].vec2d2.u = t.t * (outside_tex[0].u - inside_tex[0].u) + inside_tex[0].u;
            out[0].vec2d2.v = t.t * (outside_tex[0].v - inside_tex[0].v) + inside_tex[0].v;
            out[0].vec2d2.w = t.t * (outside_tex[0].w - inside_tex[0].w) + inside_tex[0].w;
            
            out[0].vec3d3 = vectorIntersectPlane(plane_p, plane_n, inside_points[0], outside_points[1], t);
            out[0].vec2d3.u = t.t * (outside_tex[1].u - inside_tex[0].u) + inside_tex[0].u;
            out[0].vec2d3.v = t.t * (outside_tex[1].v - inside_tex[0].v) + inside_tex[0].v;
            out[0].vec2d3.w = t.t * (outside_tex[1].w - inside_tex[0].w) + inside_tex[0].w;
            return 1;
        }
        
        if(nInsidePointCount == 2 && nOutsidePointCount == 1)
        {
            ExtraData t = new ExtraData(0);
            
            out[0].col = in.col;
            out[0].tex = in.tex;
            out[0].dp = in.dp;
            out[0].vec3d = inside_points[0];
            out[0].vec3d2 = inside_points[1];
            out[0].vec2d = inside_tex[0];
            out[0].vec2d2 = inside_tex[1];
            
            out[0].vec3d3 = vectorIntersectPlane(plane_p, plane_n, inside_points[0], outside_points[0], t);
            out[0].vec2d3.u = t.t * (outside_tex[0].u - inside_tex[0].u) + inside_tex[0].u;
            out[0].vec2d3.v = t.t * (outside_tex[0].v - inside_tex[0].v) + inside_tex[0].v;
            out[0].vec2d3.w = t.t * (outside_tex[0].w - inside_tex[0].w) + inside_tex[0].w;
            
            out[1].col = in.col;
            out[1].tex = in.tex;
            out[1].dp = in.dp;
            out[1].vec3d = inside_points[1];
            out[1].vec2d = inside_tex[1];
            out[1].vec3d2 = out[0].vec3d3; 
            out[1].vec2d2 = out[0].vec2d3;
            out[1].vec3d3 = vectorIntersectPlane(plane_p, plane_n, inside_points[1], outside_points[0], t);
            out[1].vec2d3.u = t.t * (outside_tex[0].u - inside_tex[1].u) + inside_tex[1].u;
            out[1].vec2d3.v = t.t * (outside_tex[0].v - inside_tex[1].v) + inside_tex[1].v;
            out[1].vec2d3.w = t.t * (outside_tex[0].w - inside_tex[1].w) + inside_tex[1].w;
            return 2;
        }
        
        return 0;
    }
    
    public double dist(Vec3D p, Vec3D plane_n, Vec3D plane_p)
    {
        return(plane_n.x * p.x + plane_n.y * p.y + plane_n.z * p.z - dotProduct(plane_n, plane_p));
    }
}
