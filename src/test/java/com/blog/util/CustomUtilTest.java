package com.blog.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class CustomUtilTest {
    @Test
    public void returnDescriptionWhenDescriptionIsExist() {
        String desc = CustomUtil.getDescription("desc", "");
        assertEquals(desc, "desc");
    }

    @Test
    public void returnContentWhenDescriptionIsNotExist() {
        String desc = CustomUtil.getDescription("", "content");
        assertEquals(desc, "content");
    }

    @Test
    public void returnSubstringWhenDescriptionIsNotExistAndContentLengthOverflow20() {
        String testContent = "returnSubstringWhenDescriptionIsNotExistAndContentLengthOverflow20";
        String desc = CustomUtil.getDescription("", testContent);
        assertEquals(desc, testContent.substring(0, 20));
    }
}
