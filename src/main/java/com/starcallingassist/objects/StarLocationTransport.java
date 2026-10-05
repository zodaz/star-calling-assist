package com.starcallingassist.objects;

import com.starcallingassist.enums.TransportType;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class StarLocationTransport
{
	private String transportName;
	private TransportType transportType;
}