package be.arndep.camel.shared.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonassert.JsonAssert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * @author arnaud.deprez
 * @since 4/04/16
 */
public class ObjectMapperFactoryTest {
	private static ObjectMapper mapper;

	@BeforeClass
	public static void setUpClass() throws Exception {
		mapper = ObjectMapperFactory.newInstance();
	}

	private static TestDataObjectAssert assertThat(TestDataObject data) {
		return TestDataObjectAssert.assertThat(data);
	}

	@Test
	public void testMarshallWithOptional() throws Exception {
		LocalDateTime date = LocalDateTime.of(2016, 4, 4, 16, 49, 33);

		TestDataObject obj = ImmutableTestDataObject.builder()
			.date(date)
			.string("a string")
			.optString("an optional string")
			.build();

		String out = mapper.writeValueAsString(obj);
		System.out.println("out = " + out);

		JsonAssert.with(out)
			.assertEquals("$.date", date.format(DateTimeFormatter.ISO_DATE_TIME))
			.assertEquals("$.string", "a string")
			.assertEquals("$.optString", "an optional string");
	}

	@Test
	public void testMarshallWithEmptyOptional() throws Exception {
		LocalDateTime date = LocalDateTime.of(2016, 4, 4, 16, 49, 33);

		TestDataObject obj = ImmutableTestDataObject.builder()
			.date(date)
			.string("a string")
			.build();

		String out = mapper.writeValueAsString(obj);
		System.out.println("out = " + out);

		JsonAssert.with(out)
			.assertEquals("$.date", date.format(DateTimeFormatter.ISO_DATE_TIME))
			.assertEquals("$.string", "a string")
			.assertNotDefined("$.optString");
	}

	@Test
	public void testUnmarshallWithOptional() throws Exception {
		final String json = "{\"string\":\"a string\",\"date\":\"2016-04-04T16:49:33.598\",\"optString\":\"an optional string\"}";

		TestDataObject obj = mapper.readValue(json, TestDataObject.class);
		assertThat(obj)
			.isNotNull()
			.hasString("a string")
			.hasDate(LocalDateTime.of(2016, 4, 4, 16, 49, 33, 598_000_000))
			.hasOptString(Optional.of("an optional string"));
	}

	@Test
	public void testUnmarshallWithEmptyOptional() throws Exception {
		final String json = "{\"string\":\"a string\",\"date\":\"2016-04-04T16:49:33.598\"}";

		TestDataObject obj = mapper.readValue(json, TestDataObject.class);
		assertThat(obj)
			.isNotNull()
			.hasString("a string")
			.hasDate(LocalDateTime.of(2016, 4, 4, 16, 49, 33, 598_000_000))
			.hasOptString(Optional.empty());
	}
}