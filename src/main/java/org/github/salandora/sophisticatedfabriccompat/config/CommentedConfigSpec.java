package org.github.salandora.sophisticatedfabriccompat.config;

import com.electronwill.nightconfig.core.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CommentedConfigSpec extends ConfigSpec {
	private String commentStr;

	public CommentedConfigSpec() {
		super(CommentedConfig.of(LinkedHashMap::new, InMemoryCommentedFormat.withUniversalSupport()));
		this.commentStr = "";
	}

	private CommentedConfig getCommentedConfig() {
		return (CommentedConfig) storage;
	}

	@Override
	public void define(List<String> path, Object defaultValue, Predicate<Object> validator) {
		super.define(path, defaultValue, validator);
		if (!commentStr.isEmpty()) {
			getCommentedConfig().setComment(path, commentStr);
			this.commentStr = "";
		}
	}

	@Override
	public void define(List<String> path, Supplier<?> defaultValueSupplier, Predicate<Object> validator) {
		super.define(path, defaultValueSupplier, validator);
		if (!commentStr.isEmpty()) {
			getCommentedConfig().setComment(path, commentStr);
			this.commentStr = "";
		}
	}

	public CommentedConfigSpec comment(String comment) {
		this.commentStr = comment;
		return this;
	}

	public void putAllComments(CommentedConfig config) {
		config.putAllComments(getCommentedConfig());
	}
}
