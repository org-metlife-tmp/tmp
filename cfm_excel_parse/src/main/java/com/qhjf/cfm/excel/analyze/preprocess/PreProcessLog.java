package com.qhjf.cfm.excel.analyze.preprocess;

import java.io.Serializable;

/**
 * 解析Excel之前，先清理null单元格、有标注的单元格
 * 前置处理日志：记录清理null单元格个数，清理标注个数
 * @author CHT
 *
 */
public class PreProcessLog implements Serializable {
	private static final long serialVersionUID = -7578622176626505258L;
	private Long blankCell = 0l;
	private Long commentCell = 0l;
	private Long resetCell = 0l;

	public Long getBlankCell() {
		return blankCell;
	}

	public void blankCellIncrassation() {
		this.blankCell++;
	}

	public Long getCommentCell() {
		return commentCell;
	}

	public void cellCommentIncrassation() {
		this.commentCell++;
	}

	public Long getResetCell() {
		return resetCell;
	}

	public void cellResetIncrassation() {
		this.resetCell++;
	}
}
