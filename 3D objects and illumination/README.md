# BU CS 480/680 Computer Graphics

## Lab 10

In this lab, you are required to use illumination model learned from lecture to light our sphere, which is implemented in Lab09. Here are some instructions you might need to finish this lab:
1. We defined a Material class to place our ka, kd, ks. These parameters will be used in later illumination.
2. We use infinite light here for lighting. For infinite light, we only need to define its lighting direction and light source color.
3. We use flat shading in this lab. That means for a surface(triangle) we wanna draw, we assign all 3 vertices the same color, which is calculated out of this surface's normal, lighting direction, viewing vector and surface material.  
4. You need to finish method applyLight() under LightInfinite.java, in which you should calculate ambient, diffuse and specular and return a color which combines all these three.
