/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.cosmetics.layers.models;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.model.*;

public class EarModelBox extends ModelBox {

    /** An array of 6 TexturedQuads, one for each face of a cube */
    private TexturedQuad[] quadList;
    /** X vertex coordinate of lower box corner */
    public float posX1;
    /** Y vertex coordinate of lower box corner */
    public float posY1;
    /** Z vertex coordinate of lower box corner */
    public float posZ1;
    /** X vertex coordinate of upper box corner */
    public float posX2;
    /** Y vertex coordinate of upper box corner */
    public float posY2;
    /** Z vertex coordinate of upper box corner */
    public float posZ2;
    public String boxName;


    public EarModelBox(ModelRenderer renderer, int texU, int texV, float x, float y, float z, int dx, int dy, int dz, float delta) {
        super(renderer, texU, texV, x, y, z, dx, dy, dz, delta, renderer.mirror);
    }

    /** This exists in order to allow for a ModelBox to be textured from two separate areas of the skin, in order to allow for the use of more of the limited areas of transparency on the skin file.
     * This will only work on DZ=1,2 otherwise things may not work as intended. Basically,
     * @param renderer mirrored or not mirrored? not sure what this means.
     * @param texOffsetX1 The X coord for the top left corner of the first of the two areas on the texture
     * @param texOffsetY1 The Y coord for the top left corner of the first of the two areas on the texture
     * @param texOffsetX2 The X coord for the top left corner of the second of the two areas on the texture
     * @param texOffsetY2 The Y coord for the top left corner of the second of the two areas on the texture
     * @param x offset. For solving vertex positions, ignore this (maybe something to do with position on character model?)
     * @param y offset. For solving vertex positions, ignore this (maybe something to do with position on character model?)
     * @param z offset. For solving vertex positions, ignore this (maybe something to do with position on character model?)
     * @param dx Width of the overall box
     * @param dy Height of the overall box
     * @param dz Depth of the overall box
     * @param delta Good f--king question.
     */
    public EarModelBox(EarModelRenderer renderer, int texOffsetX1, int texOffsetY1, int texOffsetX2, int texOffsetY2, float x, float y, float z, int dx, int dy, int dz, float delta, boolean mirror) {
        this(renderer.returnRenderer(), texOffsetX1, texOffsetY1, x, y, z, dx, dy, dz, delta);

        this.posX1 = x;
        this.posY1 = y;
        this.posZ1 = z;
        this.posX2 = x + (float)dx;
        this.posY2 = y + (float)dy;
        this.posZ2 = z + (float)dz;
        this.quadList = new TexturedQuad[6];
        float f = x + (float)dx;
        float f1 = y + (float)dy;
        float f2 = z + (float)dz;
        x = x - delta;
        y = y - delta;
        z = z - delta;
        f = f + delta;
        f1 = f1 + delta;
        f2 = f2 + delta;

        // I hope to god this isn't relevant for this
        PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex = new PositionTextureVertex(f, y, z, 0.0F, 8.0F);
        PositionTextureVertex positiontexturevertex1 = new PositionTextureVertex(f, f1, z, 8.0F, 8.0F);
        PositionTextureVertex positiontexturevertex2 = new PositionTextureVertex(x, f1, z, 8.0F, 0.0F);
        PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(x, y, f2, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(f, y, f2, 0.0F, 8.0F);
        PositionTextureVertex positiontexturevertex5 = new PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
        PositionTextureVertex positiontexturevertex6 = new PositionTextureVertex(x, f1, f2, 8.0F, 0.0F);

        // Ok, so here it uses the information about the coords to pull data from the skin to texture the model.
        // The math is really cool, but a bit convoluted. Here is a spreadsheet that you can use to mess with it
        // interactively: https://docs.google.com/spreadsheets/d/1bLq-26mOE52BWusWeLWy6tvJmWfT73o3lCGFPZR40aM/edit?usp=sharing
        // https://l.jakecover.me/wynntils-extended-model-box-sheet

        // x1y1 based
        this.quadList[0] = new TexturedQuad(new PositionTextureVertex[] { positiontexturevertex4, positiontexturevertex, positiontexturevertex1, positiontexturevertex5 },
                texOffsetX1 + dz + dx, texOffsetY1 + dz, texOffsetX1 + dz + dx + dz, texOffsetY1 + dz + dy, renderer.textureWidth, renderer.textureHeight);
        this.quadList[1] = new TexturedQuad(new PositionTextureVertex[] { positiontexturevertex7, positiontexturevertex3, positiontexturevertex6, positiontexturevertex2 },
                texOffsetX1, texOffsetY1 + dz, texOffsetX1 + dz, texOffsetY1 + dz + dy, renderer.textureWidth, renderer.textureHeight);
        this.quadList[2] = new TexturedQuad(new PositionTextureVertex[] { positiontexturevertex4, positiontexturevertex3, positiontexturevertex7, positiontexturevertex },
                texOffsetX1 + dz, texOffsetY1, texOffsetX1 + dz + dx, texOffsetY1 + dz, renderer.textureWidth, renderer.textureHeight);
        this.quadList[4] = new TexturedQuad(new PositionTextureVertex[] { positiontexturevertex, positiontexturevertex7, positiontexturevertex2, positiontexturevertex1 },
                texOffsetX1 + dz, texOffsetY1 + dz, texOffsetX1 + dz + dx, texOffsetY1 + dz + dy, renderer.textureWidth, renderer.textureHeight);

        // x2y2 based
        this.quadList[3] = new TexturedQuad(new PositionTextureVertex[] { positiontexturevertex1, positiontexturevertex2, positiontexturevertex6, positiontexturevertex5 },
                texOffsetX2, texOffsetY2, texOffsetX2 + dx, texOffsetY2 + dz, renderer.textureWidth, renderer.textureHeight);
        this.quadList[5] = new TexturedQuad(new PositionTextureVertex[] { positiontexturevertex3, positiontexturevertex4, positiontexturevertex5, positiontexturevertex6 },
                texOffsetX2, texOffsetY2 + dz, texOffsetX2 + dx, texOffsetY1 + dz + dy, renderer.textureWidth, renderer.textureHeight);
    }

    @SideOnly(Side.CLIENT)
    public void render(BufferBuilder renderer, float scale) {
        for (TexturedQuad texturedquad : this.quadList) {
            texturedquad.draw(renderer, scale);
        }
    }

    public EarModelBox setBoxName(String name) {
        this.boxName = name;

        return this;
    }

}
