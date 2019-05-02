package io.dropwizard.jetty;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.Deflater;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

import io.dropwizard.util.Size;

public class GzipFilterFactory {
    private boolean enabled = true;

    @NotNull
    private Size minimumEntitySize = Size.bytes(256);

    @NotNull
    private Size bufferSize = Size.kilobytes(8);

    private Set<Pattern> excludedUserAgentPatterns = Sets.newHashSet();
    private Set<String> compressedMimeTypes = Sets.newHashSet();
    private Set<String> includedMethods = Sets.newHashSet();
    private boolean gzipCompatibleDeflation = true;
    private String vary = "Accept-Encoding";

    @Min(Deflater.DEFAULT_COMPRESSION)
    @Max(Deflater.BEST_COMPRESSION)
    private int deflateCompressionLevel = Deflater.DEFAULT_COMPRESSION;

    @JsonProperty
    public boolean isEnabled() {
        return enabled;
    }

    @JsonProperty
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @JsonProperty
    public Size getMinimumEntitySize() {
        return minimumEntitySize;
    }

    @JsonProperty
    public void setMinimumEntitySize(Size size) {
        this.minimumEntitySize = checkNotNull(size);
    }

    @JsonProperty
    public Size getBufferSize() {
        return bufferSize;
    }

    @JsonProperty
    public void setBufferSize(Size size) {
        this.bufferSize = checkNotNull(size);
    }
    
    @JsonProperty
    public Set<String> getCompressedMimeTypes() {
        return compressedMimeTypes;
    }

    @JsonProperty
    public void setCompressedMimeTypes(Set<String> mimeTypes) {
        this.compressedMimeTypes = mimeTypes;
    }

    @JsonProperty
    public int getDeflateCompressionLevel() {
        return deflateCompressionLevel;
    }

    @JsonProperty
    public void setDeflateCompressionLevel(int level) {
        this.deflateCompressionLevel = level;
    }

    @JsonProperty
    public boolean isGzipCompatibleDeflation() {
        return gzipCompatibleDeflation;
    }

    @JsonProperty
    public void setGzipCompatibleDeflation(boolean compatible) {
        this.gzipCompatibleDeflation = compatible;
    }

    @JsonProperty
    public Set<Pattern> getExcludedUserAgentPatterns() {
        return excludedUserAgentPatterns;
    }

    @JsonProperty
    public void setExcludedUserAgentPatterns(Set<Pattern> patterns) {
        this.excludedUserAgentPatterns = patterns;
    }

    @JsonProperty
    public Set<String> getIncludedMethods() {
        return includedMethods;
    }

    @JsonProperty
    public void setIncludedMethods(Set<String> methods) {
        this.includedMethods = methods;
    }

    @JsonProperty
    public String getVary() {
        return vary;
    }

    @JsonProperty
    public void setVary(String vary) {
        this.vary = vary;
    }

    public GzipHandler build() {
        final GzipHandler filter = new GzipHandler() {
            @Override
            protected HttpField getVaryField() {
                if (vary != null) {
                    return new HttpField(HttpHeader.VARY, vary);
                }
                return super.getVaryField();
            }
        };
        
        filter.setMinGzipSize((int) minimumEntitySize.toBytes());
        
        filter.setInflateBufferSize((int) bufferSize.toBytes());
        
        filter.setCompressionLevel(deflateCompressionLevel);
        
        if (compressedMimeTypes != null) {
            filter.addIncludedMimeTypes(compressedMimeTypes.toArray(new String[0]));
        }

        if (includedMethods != null) {
            filter.addIncludedMethods(includedMethods.toArray(new String[0]));
        }

        if (excludedUserAgentPatterns != null) {
            filter.addExcludedAgentPatterns(excludedUserAgentPatterns.toArray(new String[0]));
        }

        filter.setCheckGzExists(gzipCompatibleDeflation);
        return filter;
    }
}
