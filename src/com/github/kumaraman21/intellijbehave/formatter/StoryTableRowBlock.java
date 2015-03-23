package com.github.kumaraman21.intellijbehave.formatter;

import com.github.kumaraman21.intellijbehave.parser.IStoryPegElementType;
import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DeBritoD on 20.03.2015.
 */
public class StoryTableRowBlock extends AbstractBlock {
    private int[] columnWidths;

    protected StoryTableRowBlock(int[] columnWidths, ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment) {
        super(node, wrap, alignment);
        this.columnWidths = columnWidths;
    }

    @Override
    protected List<Block> buildChildren() {
        List<Block> retVal = new ArrayList<Block>();
        int columnNr = 0;
        ASTNode node = getNode();
        ASTNode[] children = node.getChildren(
                TokenSet.create(IStoryPegElementType.STORY_TOKEN_PIPE, IStoryPegElementType.STORY_TABLE_CELL,
                        IStoryPegElementType.STORY_TABLE_CELL_EMPTY));
        for (ASTNode child : children) {
            IElementType elementType = child.getElementType();
            if (elementType == IStoryPegElementType.STORY_TABLE_CELL_EMPTY) {
                ++columnNr;
            } else if (elementType == IStoryPegElementType.STORY_TABLE_CELL) {
                retVal.add(new StoryTableCellBlock(columnNr, child, null, null));
                ++columnNr;
            } else if (elementType == IStoryPegElementType.STORY_TOKEN_PIPE) {
                retVal.add(new StoryTableDelimiterBlock(columnNr, child, null, null));
            }
        }
        return retVal;
    }

    @Nullable
    @Override
    public Spacing getSpacing(Block child1, Block child2) {
        //Spacing.createSpacing()
        if (child1 == null && child2 instanceof StoryTableDelimiterBlock) {
            return Spacing.createSpacing(0, 0, 0, true, 0);
        }
        if (child1 instanceof StoryTableDelimiterBlock && child2 instanceof StoryTableCellBlock) {
            return Spacing.createSpacing(1, 1, 0, true, 0);
        }
        if (child1 instanceof StoryTableDelimiterBlock && child2 instanceof StoryTableDelimiterBlock) {
            StoryTableDelimiterBlock cell = (StoryTableDelimiterBlock) child1;
            int columnNr = cell.getColumnNr();
            int width = columnWidths[columnNr] + 2;
            return Spacing.createSpacing(width, width, 0, true, 0);
        }
        if (child1 instanceof StoryTableCellBlock && child2 instanceof StoryTableDelimiterBlock) {
            StoryTableCellBlock cell = (StoryTableCellBlock) child1;
            String text = cell.getText();
            int length = text.length();
            int width = columnWidths[cell.getColumnNr()] + 1;
            int diff = width - length;
            return Spacing.createSpacing(diff, diff, 0, true, 0);
        }
        return null;
    }
    @Override
    public Indent getIndent() {
        return Indent.getNoneIndent();
    }

    @Override
    public boolean isLeaf() {
        return false;
    }
}