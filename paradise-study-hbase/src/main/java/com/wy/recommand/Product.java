package com.wy.recommand;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 产品类
 * 
 * @author ParadiseWY
 * @date 2020-10-15 14:01:47
 * @git {@link https://github.com/mygodness100}
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer productId;

	private String productName;
}
