package net.acesinc.data.json.generator.transport;

public class Impression {

	private Integer adGroupId;
	private Integer adId;
	private String domain;
	private boolean clicked;

	public Impression() {
	}

	public Impression(final Integer adGroupId, final Integer adId, final String domain, final boolean clicked) {
		this.adGroupId = adGroupId;
		this.adId = adId;
		this.domain = domain;
		this.clicked = clicked;
	}

	public Integer getAdGroupId() {
		return adGroupId;
	}

	public void setAdGroupId(final Integer adGroupId) {
		this.adGroupId = adGroupId;
	}

	public Integer getAdId() {
		return adId;
	}

	public void setAdId(final Integer adId) {
		this.adId = adId;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(final String domain) {
		this.domain = domain;
	}

	public boolean isClicked() {
		return clicked;
	}

	public void setClicked(final boolean clicked) {
		this.clicked = clicked;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("Impression{");
		sb.append("adGroupId=").append(adGroupId);
		sb.append(", adId=").append(adId);
		sb.append(", domain='").append(domain).append('\'');
		sb.append(", clicked=").append(clicked);
		sb.append('}');
		return sb.toString();
	}
}
