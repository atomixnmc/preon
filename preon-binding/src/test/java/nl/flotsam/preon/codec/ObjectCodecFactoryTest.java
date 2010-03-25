/**
 * Copyright (C) 2009-2010 Wilfred Springer
 *
 * This file is part of Preon.
 *
 * Preon is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 *
 * Preon is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Preon; see the file COPYING. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is making a
 * combined work based on this library. Thus, the terms and conditions of the
 * GNU General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules, and
 * to copy and distribute the resulting executable under terms of your choice,
 * provided that you also meet, for each linked independent module, the terms
 * and conditions of the license of that module. An independent module is a
 * module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but
 * you are not obligated to do so. If you do not wish to do so, delete this
 * exception statement from your version.
 */
package nl.flotsam.preon.codec;

import nl.flotsam.preon.*;
import nl.flotsam.preon.annotation.Bound;
import nl.flotsam.preon.annotation.BoundObject;
import nl.flotsam.preon.annotation.Choices;
import nl.flotsam.preon.annotation.TypePrefix;
import nl.flotsam.preon.buffer.BitBuffer;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.AnnotatedElement;

import static junit.framework.Assert.fail;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ObjectCodecFactoryTest {

    private AnnotatedElement metadata;

    private CodecFactory delegate;

    private BitBuffer buffer;

    private Resolver resolver;

    private Builder builder;

    private BoundObject settings;

    private Choices choices;

    @Before
    public void setUp() {
        metadata = createMock(AnnotatedElement.class);
        delegate = createMock(CodecFactory.class);
        buffer = createMock(BitBuffer.class);
        resolver = createMock(Resolver.class);
        settings = createMock(BoundObject.class);
        builder = createMock(Builder.class);
        choices = createMock(Choices.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBoundObjectNoMembersTwoTypesNoPrefix() throws DecodingException {
        Codec codec1 = createMock(Codec.class);
        Codec codec2 = createMock(Codec.class);
        expect(metadata.isAnnotationPresent(Bound.class)).andReturn(false).anyTimes();
        expect(metadata.isAnnotationPresent(BoundObject.class)).andReturn(true).anyTimes();
        expect(metadata.getAnnotation(BoundObject.class)).andReturn(settings);
        expect(settings.selectFrom()).andReturn(choices).times(2);
        expect(choices.alternatives()).andReturn(new Choices.Choice[0]);
        expect(choices.defaultType()).andReturn((Class) Void.class);
        expect(settings.type()).andReturn((Class) Void.class);
        expect(settings.types()).andReturn(new Class[]{TestObject1.class, TestObject2.class})
                .times(2);
        expect(
                delegate.create((AnnotatedElement) EasyMock.isNull(), EasyMock.isA(Class.class),
                        (ResolverContext) EasyMock.isNull())).andReturn(codec1);
        expect(codec1.getTypes()).andReturn(new Class[]{TestObject1.class});
        expect(codec2.getTypes()).andReturn(new Class[]{TestObject2.class});
        expect(
                delegate.create((AnnotatedElement) EasyMock.isNull(), EasyMock.isA(Class.class),
                        (ResolverContext) EasyMock.isNull())).andReturn(codec2);
        replay(metadata, delegate, buffer, resolver, settings, codec1, codec2, choices);
        ObjectCodecFactory factory = new ObjectCodecFactory(delegate);
        try {
            Codec<TestObject1> created = factory.create(metadata, TestObject1.class, null);
            fail("Expecting failure due to missing prefixes.");
        } catch (CodecConstructionException cce) {
            // What we expect.
        }
        verify(metadata, delegate, buffer, resolver, settings, choices);
    }

    @Test
    public void testBoundObjectNoMembersTwoTypesWithPrefix() throws DecodingException {
        Codec codecTest3 = createMock(Codec.class);
        Codec codecTest4 = createMock(Codec.class);
        expect(metadata.isAnnotationPresent(Bound.class)).andReturn(false).anyTimes();
        expect(metadata.isAnnotationPresent(BoundObject.class)).andReturn(true).anyTimes();
        expect(settings.selectFrom()).andReturn(choices).times(2);
        expect(choices.alternatives()).andReturn(new Choices.Choice[0]);
        expect(choices.defaultType()).andReturn((Class) Void.class);
        expect(metadata.getAnnotation(BoundObject.class)).andReturn(settings);
        expect(settings.type()).andReturn((Class) Void.class);
        expect(settings.types()).andReturn(new Class[]{TestObject3.class, TestObject4.class})
                .times(2);
        expect(delegate.create(null, TestObject3.class, null)).andReturn(codecTest3);
        expect(codecTest3.getTypes()).andReturn(new Class<?>[]{TestObject3.class});
        expect(codecTest4.getTypes()).andReturn(new Class<?>[]{TestObject4.class});
        expect(delegate.create(null, TestObject4.class, null)).andReturn(codecTest4);
        // expect(codecTest3.getSize(resolver)).andReturn(6);
        // expect(codecTest4.getSize(resolver)).andReturn(6);
        expect(buffer.readAsLong(8)).andReturn(0L);
        expect(codecTest3.decode(buffer, resolver, builder)).andReturn(new TestObject3());
        replay(metadata, delegate, buffer, resolver, settings, codecTest3, codecTest4, builder,
                choices);
        ObjectCodecFactory factory = new ObjectCodecFactory(delegate);
        Codec<TestObject1> codec = factory.create(metadata, TestObject1.class, null);
        assertNotNull(codec);
        TestObject1 result = codec.decode(buffer, resolver, builder);
        assertNotNull(result);
        assertTrue(!(result instanceof TestObject4));
        assertTrue(result instanceof TestObject3);
        verify(metadata, delegate, buffer, resolver, settings, codecTest3, codecTest4, builder,
                choices);
    }

    public static class TestObject1 {

    }

    public static class TestObject2 {

    }

    @TypePrefix(size = 8, value = "0")
    public static class TestObject3 extends TestObject1 {

    }

    @TypePrefix(size = 8, value = "1")
    public static class TestObject4 extends TestObject1 {

    }

}
